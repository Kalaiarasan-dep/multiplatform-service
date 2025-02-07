package in.hashconnect.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.storage.StorageService;
import in.hashconnect.storage.vo.FileContent;
import in.hashconnect.util.StringUtil;

public class S3Util {
	private static final Logger logger = LoggerFactory.getLogger(S3Util.class);
	private static final String RANDOM_STRING_GROUP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	public static String upload(StorageService fileStorage, ExcelBuilder builder, Date expirationTime) {
		return upload(fileStorage, builder, expirationTime, "dell-reports", null, null);
	}

	public static String upload(StorageService fileStorage, ExcelBuilder builder, Date expirationTime, String bucket,
			String folder, String ext) {
		Path tmpFile = null;
		try {
			tmpFile = Files.createTempFile("report", String.valueOf(System.currentTimeMillis()) + ".xlsx",
					new FileAttribute<?>[] {});

			logger.info("tmp location " + tmpFile);

			// write byes to tmp location this is to avoid storing in memory
			try (OutputStream out = Files.newOutputStream(tmpFile, StandardOpenOption.WRITE)) {
				builder.writeToOutputStream(out);
			}

			// get exact size
			long size = 0, read = -1;
			try (InputStream in = Files.newInputStream(tmpFile, StandardOpenOption.READ)) {
				while ((read = in.read(new byte[1000])) != -1)
					size += read;
			}

			String fileName = StringUtil.concate("report_", RandomStringUtils.random(10, RANDOM_STRING_GROUP), "_",
					System.currentTimeMillis(), StringUtil.emptyIfNull(ext));
			// read from tmpFile and start the upload process
			try (InputStream in = Files.newInputStream(tmpFile, StandardOpenOption.READ)) {
				FileContent fc = new FileContent();
				fc.setBucket(bucket);
				if (StringUtil.isValid(folder))
					fc.setFolder(folder);
				fc.setName(fileName);
				fc.setInStream(in);
				fc.setLength(size);
				fc.setExpirationTime(expirationTime);
				fileStorage.put(fc);
			}

			logger.info("uploaded is successful");

			return fileName;
		} catch (Exception e) {
			throw new RuntimeException("failed to upload to storage", e);
		} finally {
			delete(tmpFile);
		}
	}

	private static void delete(Path path) {
		if (path != null) {
			try {
				Files.delete(path);
			} catch (IOException e) {
				throw new RuntimeException("failed to delete tmpfile", e);
			}
		}
	}

	public static FileContent download(StorageService fileStorage, String bucket, String file) {
		try {
			FileContent fc = new FileContent();
			fc.setBucket(bucket);
			fc.setName(file);
			fileStorage.get(fc);

			return fc;
		} catch (Exception e) {
			throw new RuntimeException("failed to upload to storage", e);
		}
	}

	public static void delete(StorageService fileStorage, String bucket, String file) {
		try {
			FileContent fc = new FileContent();
			fc.setBucket(bucket);
			fc.setName(file);
			fileStorage.delete(fc);
		} catch (Exception e) {
			throw new RuntimeException("failed to delete to storage", e);
		}
	}
}
