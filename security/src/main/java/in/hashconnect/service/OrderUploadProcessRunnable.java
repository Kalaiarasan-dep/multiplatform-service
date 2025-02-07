package in.hashconnect.service;

import static in.hashconnect.util.StringUtil.convertToString;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.collections4.MapUtils.isEmpty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.BulkImportDao;
import in.hashconnect.excel.reader.Row;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;

public class OrderUploadProcessRunnable implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(OrderUploadProcessRunnable.class);

	private Map<String, Object> request;
	private Response<?> response;
	// DAOs
	private BulkImportDao bulkImportDao;
	private SettingsUtil settingsUtil;
	private NotificationServiceFactory notificationServiceFactory;

	private static final Integer STATUS_NO_REF = 13;
	private static final Integer STATUS_NEW = 4;

	private boolean completed;
	private boolean responded;

	// sync object
	private Object sync;

	public OrderUploadProcessRunnable(Object sync, Map<String, Object> request, BulkImportDao bulkImportDao,
			SettingsUtil settingsUtil, NotificationServiceFactory notificationServiceFactory) {
		this.request = request;
		this.sync = sync;
		// DAOs
		this.settingsUtil = settingsUtil;
		this.bulkImportDao = bulkImportDao;
		this.notificationServiceFactory = notificationServiceFactory;
	}

	@Override
	public void run() {
		Integer refId = getInteger(request, "refId");
		logger.debug("starting refId: {}", refId);

		try {
			response = process();
		} catch (Exception e) {
			logger.error("order upload has failed for refId: " + refId, e);
		} finally {
			logger.info("completed with refId: {}", refId);

			completed = true;

			if (responded) {
				// we should send notification to user.

				Map<String, Object> fileDetails = bulkImportDao.getFileDetails(getInteger(request, "refId"));
				// format date time
				LocalDateTime crDt = (LocalDateTime) getObject(fileDetails, "created_dt");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
				String createdDt = crDt.format(formatter);

				// creating data
				Map<String, Object> data = new HashMap<>(fileDetails);
				data.put("created_dt", createdDt);
				data.put("status", response.getStatus());
				data.put("desc", response.getDesc());

				notificationServiceFactory.get(TYPE.EMAIL).process(new Notification(
						settingsUtil.getValue("order_upload_delayed_template"), getString(data, "email"), data));

				// no point in notifying hence return from here.
				return;
			}

			// not yet responded, notify the waiter now
			synchronized (sync) {
				sync.notify();
			}
		}
	}

	private Response<?> process() {
		Integer refId = getInteger(request, "refId");
		if (refId == null) {
			return Response.failed("invalid refId");
		}

		Map<String, Object> uploadedData = bulkImportDao.getDataToValidateByRefId(refId);
		if (isEmpty(uploadedData)) {
			logger.info("invalid refId: {}", refId);
			return Response.failed("invalid refId");
		}

		// get file data
		String json = getString(uploadedData, "json");
		List<Row> rows = JsonUtil.readValue(json, new TypeReference<List<Row>>() {
		});

		logger.info("json rows for refId: {}, size: {}", refId, rows != null ? rows.size() : null);

		// mapped fields
		List<Map<String, Object>> mappedFields = JsonUtil.readValue(getString(uploadedData, "mapped_fields"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		// fixing data
		List<String> ids = rows.parallelStream().map(r -> {
			if (r.isHeader())
				return null;

			mappedFields.stream().forEach(m -> {
				List<Object> values = r.getColumnValues();

				Integer idx = getInteger(m, "idx");
				Object v = idx == null ? null : getValue(values, idx);

				if (v != null && v instanceof Double && "string".equals(getString(m, "data_type"))) {
					long l = ((Double) v).longValue();
					values.set(idx, l);
				}
			});

			return convertToString(getValue(r.getColumnValues(), 1));
		}).filter(r -> isValid(r)).collect(Collectors.toList());

		Map<String, Integer> partnerDetails = bulkImportDao
				.findPartnerDetailsByProgramAndMorCId(getString(uploadedData, "program_id"), ids);

		// prepare data to save as bulk
		List<Object[]> rowsToSave = Collections.synchronizedList(new ArrayList<>(rows.size()));
		rows.parallelStream().forEach(r -> {
			if (r.isHeader()) {
				return;
			}

			List<Object> colValues = r.getColumnValues();
			String merchantId = convertToString(getValue(colValues, 1));

			Integer partnerId = partnerDetails.get(merchantId);
			Integer statusId = partnerId == null ? STATUS_NO_REF : STATUS_NEW;

			List<Object> rowToSave = new ArrayList<>(colValues.size() + 4);
			rowToSave.add(refId);
			rowToSave.add(partnerId);
			rowToSave.add(statusId);
			rowToSave.add(getString(uploadedData, "program_id"));

			mappedFields.stream().forEach(m -> {
				Integer idx = getInteger(m, "idx");
				if (idx != null)
					rowToSave.add(getValue(colValues, getInteger(m, "idx")));
			});

			rowsToSave.add(rowToSave.toArray());
		});

		logger.info("rowsToSave: {} on refId: {}", rowsToSave.size(), refId);

		// save to db
		if (!bulkImportDao.importToBulkUpload(refId, mappedFields, rowsToSave)) {
			return Response.failed(settingsUtil.getValue("order_upload_failed_msg"));
		}

		Response<?> response = Response.ok();
		response.setDesc(settingsUtil.getValue("order_upload_success_msg"));
		return response;
	}

	private Object getValue(List<Object> list, int idx) {
		try {
			return list.get(idx);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public Response<?> getResponse() {
		return this.response;
	}

	public void setResponded(boolean responded) {
		this.responded = responded;
	}

}
