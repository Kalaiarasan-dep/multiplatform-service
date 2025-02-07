package in.hashconnect.service;

import static in.hashconnect.util.DateUtil.*;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.collections4.MapUtils.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.dao.BulkImportDao;
import in.hashconnect.excel.reader.Row;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.vo.BulkImportValidateVo;
import in.hashconnect.vo.BulkImportVo;
import in.hashconnect.vo.FileUploadValidateResponse;
import in.hashconnect.vo.UploadFileValidator;

@Service
@SuppressWarnings("unchecked")
public class OrderUploadProcessor extends AbstractLeadProcessor implements UploadProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BulkImportDao bulkImportDao;

	@Autowired
	private UploadFileValidator validator;

	@Autowired
	private SettingsUtil settingsUtil;

	@Autowired
	private NotificationServiceFactory notificationServiceFactory;

	private final SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
	private final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Response<BulkImportVo> bulkImport(MultipartFile file, String userId, String programId, String batchId) {

		Map<String, Object> meta = parse(file, userId, "bulk", programId, batchId);
		if (MapUtils.isEmpty(meta)) {
			return Response.failed("Failed to read file");
		}

		List<Row> rows = (List<Row>) getObject(meta, "rows");
		if (rows.size() == 1) {
			return Response.failed("No data found in excel");
		}
		Integer refId = getInteger(meta, "refId");

		// preparing list for output
		List<String> headerList = rows.get(0).getColumnValues().stream().map(v -> v.toString())
				.collect(Collectors.toList());

		List<Map<String, Object>> allColumns = bulkImportDao.getAllColumns(programId);

		// map the columns by header names
		allColumns.stream().forEach(m -> {
			String displayName = getString(m, "display_name");

			if (headerList.contains(displayName))
				m.put("mappedColumn", displayName);
		});

		bulkImportDao.mapFields(refId, allColumns);

		BulkImportVo vo = new BulkImportVo();
		vo.setRefId(refId);
		vo.setMappedFields(allColumns);
		vo.setUploadedColumns(headerList);

		Response<BulkImportVo> response = Response.ok();
		response.setData(vo);

		return response;
	}

	@Override
	public Response<BulkImportValidateVo> validate(Map<String, Object> request) {
		Response<BulkImportValidateVo> response = Response.ok();

		Integer refId = getInteger(request, "refId");

		logger.debug("validating refId: {}", refId);

		if (refId == null) {
			return Response.failed("invalid refId");
		}

		Map<String, Object> uploadedData = bulkImportDao.getDataToValidateByRefId(refId);
		if (isEmpty(uploadedData)) {
			return Response.failed("invalid refId");
		}

		String json = getString(uploadedData, "json");
		List<Row> rows = JsonUtil.readValue(json, new TypeReference<List<Row>>() {
		});

		List<String> headers = rows.get(0).getColumnValues().stream().map(v -> v.toString())
				.collect(Collectors.toList());
		// validate headernames
		BulkImportValidateVo validateVo = validator.validateHeaderNames(rows, getString(uploadedData, "program_id"));
		if (validateVo.getFailedRecords() != null && validateVo.getFailedRecords().size() > 0) {
			response.setStatus(STATUS.SUCCESS);
			response.setData(validateVo);
			return response;
		}

		List<Map<String, Object>> mappedFields = JsonUtil.readValue(getString(uploadedData, "mapped_fields"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		// find excel index
		List<Map<String, Object>> unmappedFields = new ArrayList<>(mappedFields.size());
		mappedFields.parallelStream().forEach(m -> {
			String mappedField = getString(m, "mappedColumn");
			if (isValid(mappedField)) {
				m.put("idx", headers.indexOf(mappedField));
				return;
			}
			unmappedFields.add(m);
		});
		// update index in DB
		bulkImportDao.mapFields(refId, mappedFields);

		// invalid companies
		List<Row> noCompanyList = new ArrayList<>(rows.size());

		// validate the rows
		List<Row> invalidRows = rows.parallelStream().map(r -> {
			if (r.isHeader()) {
				r.setValid(true);
				return r;
			}

			// settings true by default and then see if turn to error
			// below help if we are re-validating same file.
			r.setValid(true);

			mappedFields.parallelStream().forEach(f -> {
				Integer idx = getInteger(f, "idx");
				Object v = idx == null ? null : getValue(r.getColumnValues(), idx);

				if (getBooleanValue(f, "mandatory")) {
					if (v == null || !isValid(String.valueOf(v))) {
						r.setValid(false);
						r.setError(getString(f, "display_name") + " is mandatory field");
						return;
					}
					String regex = getString(f, "regex");
					if (isValid(regex) && !Pattern.matches(regex, String.valueOf(v))) {
						r.setValid(false);
						r.setError(getString(f, "display_name") + " having incorrect data");
						return;
					}
				}

				if (v != null && v instanceof Double && "string".equals(getString(f, "data_type"))) {
					long l = ((Double) v).longValue();
					r.getColumnValues().set(idx, l);
				}
				// convert the order date to yyyy-mm-dd and then save it
				// because date column in mysql db accepts only yyyy-mm-dd
				if (v != null && v instanceof String && "date".equals(getString(f, "data_type"))) {
					formatOrderDate(r.getColumnValues(), idx);
				}

				// check any other things to validated
				if (!r.isValid())
					return;
			});

			if (r.isValid())
				r.setError(null);

			return r;
		}).filter(r -> !r.isValid() && !noCompanyList.contains(r)).collect(Collectors.toList());

		FileUploadValidateResponse validatorResp = validator.validateRows(rows, invalidRows,
				getString(uploadedData, "program_id"));

		// update rows back to db
		bulkImportDao.updateRowsByRefId(refId, rows);
		rows.parallelStream().forEach(r -> r.setColumnValues(null));

		BulkImportValidateVo vo = new BulkImportValidateVo();
		vo.setRefId(refId);
		vo.setErrors(invalidRows);
		vo.setUnmappedFields(unmappedFields);
		vo.setTotalRecordsToImport(validatorResp.readyToPrcoess());
		vo.setNoReferencesList(validatorResp.noRefIdRecords());
		vo.setFailedRecords(validatorResp.failedRecords());

		response.setStatus(STATUS.SUCCESS);
		response.setData(vo);

		return response;
	}

	private Object getValue(List<Object> list, int idx) {
		try {
			return list.get(idx);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Response<?> process(Map<String, Object> request) {
		final Object sync = new Object();

		OrderUploadProcessRunnable processor = new OrderUploadProcessRunnable(sync, request, bulkImportDao,
				settingsUtil, notificationServiceFactory);
		new Thread(processor).start();

		// wait for completion
		synchronized (sync) {
			try {
				sync.wait(1000 * settingsUtil.getIntValue("order_upload_wait_time_in_sec"));
			} catch (InterruptedException e) {
			}
		}

		// wait is complete, see if process is complete
		if (processor.isCompleted()) {
			return processor.getResponse();
		}

		processor.setResponded(true);
		return Response.delayed(settingsUtil.getValue("order_upload_longer_msg"));
	}

	@SuppressWarnings({ "rawtypes" })
	private void formatOrderDate(List list, Integer orderDatePosition) {
		String data = (String) list.get(orderDatePosition);

		Date date = DateUtil.parse("dd-MM-yyyy", data);
		if (date != null)
			list.set(orderDatePosition, format("yyyy-MM-dd", date));

	}
}
