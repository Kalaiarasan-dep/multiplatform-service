package in.hashconnect.scheduler.report;

import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.opencsv.CSVWriter;

import in.hashconnect.excel.ExcelBuilder;
import in.hashconnect.excel.S3Util;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Attachment;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.scheduler.Job;
import in.hashconnect.scheduler.PostProcessor;
import in.hashconnect.scheduler.dao.SchedulerDao;
import in.hashconnect.scheduler.vo.ScheduledJob;
import in.hashconnect.storage.StorageService;
import in.hashconnect.util.AESUtil;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.JsonUtil;

public class ReportJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ReportJob.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private NotificationServiceFactory notificationServiceFactory;

	@Autowired
	private SchedulerDao schedulerDao;

	@Autowired
	private AESUtil aesUtil;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void execute(Map<String, Object> context) {
		if ("csv".equalsIgnoreCase(getString(context, "fileType"))) {
			// CSV is configured
			generateCSVReport(context);
			return;
		}
		if ("inlineContent".equalsIgnoreCase(getString(context, "fileType"))) {
			// CSV is configured
			generateInlineReport(context);
			return;
		}
		// build excel report
		generateReport(context);
	}

	private void generateInlineReport(Map<String, Object> context) {
		String query = getString(context, "sql");
		String mailBody = jdbcTemplate.queryForObject(query, String.class);
		context.put("SUMMARY", mailBody);
		sendMail(context, null, null);
	}

	public void generateReport(Map<String, Object> context) {
		ExcelBuilder builder = new ExcelBuilder(100).createSheet("report");
		String query = getString(context, "sql");
		Integer batchSize = getInteger(context, "batchSize");
		ScheduledJob job = (ScheduledJob) getObject(context, "job");

		int rows = 0, totalRows = 0, start = 0, limit = batchSize == null ? 500 : batchSize;
		try {
			ReportMetaData reportMetaData = new ReportMetaData();
			do {
				String reportQueryWithLimit = query + " limit " + start + ", " + limit;

				rows = namedParameterJdbcTemplate.query(reportQueryWithLimit, context,
						new ReportResultSetExtractor(builder, reportMetaData, aesUtil));

				start += limit;
				totalRows += rows;

				String message = "so far read " + totalRows;

				job.setStatus(message);
				schedulerDao.updateJobStatus(job);
			} while (rows > 0);

			postProcess(context, builder, null);
		} finally {
		}
	}

	private void postProcess(Map<String, Object> context, ExcelBuilder excelBuilder, byte[] bytes) {
		String postProcessor = getString(context, "postProcessor");
		boolean email = getBooleanValue(context, "triggerEmail");

		if (isValid(postProcessor))
			applicationContext.getBean(postProcessor, PostProcessor.class).execute(context, bytes);

		if (email)
			sendMail(context, excelBuilder, bytes);
	}

	public void generateCSVReport(Map<String, Object> context) {
		Path tmpFile = createFile();

		String query = getString(context, "sql");
		Integer batchSize = getInteger(context, "batchSize");
		ScheduledJob job = (ScheduledJob) getObject(context, "job");

		int rows = 0, totalRows = 0, start = 0, limit = batchSize == null ? 500 : batchSize;
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmpFile.toFile()));
				CSVWriter csvWriter = new CSVWriter(bufferedWriter)) {
			ReportMetaData reportMetaData = new ReportMetaData();
			do {
				String reportQueryWithLimit = query + " limit " + start + ", " + limit;

				rows = namedParameterJdbcTemplate.query(reportQueryWithLimit, context,
						new ReportCSVResultSetExtractor(csvWriter, reportMetaData, aesUtil, totalRows));

				start += limit;
				totalRows += rows;

				String message = "so far read " + totalRows;

				job.setStatus(message);
				schedulerDao.updateJobStatus(job);

				// if there are more, repeat again
			} while (rows > 0);

			// flush now
			csvWriter.flush();

			postProcess(context, null, FileUtils.readFileToByteArray(tmpFile.toFile()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				Files.delete(tmpFile);
			} catch (IOException e) {
				logger.error("failed to delete tmpFile", e);
			}
		}
	}

	private Path createFile() {
		try {
			return Files.createTempFile("temp-" + System.currentTimeMillis(), ".csv",
					PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
		} catch (Exception e) {
			throw new RuntimeException("failed to create tmp file", e);
		}
	}

	public void sendMail(Map<String, Object> params, ExcelBuilder builder, byte[] bytes) {
		String template = getString(params, "template");
		String to = getString(params, "to");
		String contentType = getString(params, "contentType");
		String fileName = getString(params, "fileName");
		boolean uploadToS3 = getBooleanValue(params, "uploadToS3");

		Notification notification = new Notification(template, to, params);

		if (uploadToS3) {
			String bucket = getString(params, "s3-bucket");
			String folder = getString(params, "s3-folder");
			Integer expiryInHr = getInteger(params, "expiry-in-hr");

			StorageService storageService = applicationContext.getBean("storageService", StorageService.class);
			String uploadedFileName = S3Util.upload(storageService, builder, null, bucket, folder, ".xlsx");

			Map<String, Object> linkAttrs = new HashMap<String, Object>();
			linkAttrs.put("location", new File(folder, uploadedFileName).getAbsolutePath());
			linkAttrs.put("bucket", bucket);
			if (expiryInHr != null)
				linkAttrs.put("expiry", DateUtil.add(new Date(), Calendar.HOUR, expiryInHr).getTime());

			String fileRef = aesUtil.encrypt(JsonUtil.toString(linkAttrs));
			params.put("fileRef", fileRef);

		} else {
			if (bytes == null && builder != null) {
				// if build is not null then get bytes of it
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				builder.writeToOutputStream(out);
				bytes = out.toByteArray();
			}

			if (bytes != null) {
				// use attachment only if bytes not null
				boolean makeZip = getBooleanValue(params, "attachAsZip");

				if (makeZip) {
					bytes = prepareZipBytes(fileName, bytes);
					contentType = "application/zip";
				}

				Attachment attachment = new Attachment(bytes, contentType, fileName);
				notification.setAttachment(attachment);
			}
		}

		notificationServiceFactory.get(Notification.TYPE.EMAIL).process(notification);
	}

	protected byte[] prepareZipBytes(String fileName, byte[] data) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ZipOutputStream zout = new ZipOutputStream(bOut);
		try {
			ZipEntry ze = new ZipEntry(fileName);
			zout.putNextEntry(ze);
			zout.write(data);
			zout.closeEntry();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				zout.close();
			} catch (IOException e) {
			}
		}
		return bOut.toByteArray();
	}
}
