package in.hashconnect.vo;

import static in.hashconnect.util.StringUtil.convert;
import static in.hashconnect.util.StringUtil.convertToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.excel.reader.Row;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;

/**
 * Validate the rows in upload file - validate the conditions for failed,
 * contract or merchant id not found in database (No ref id found)
 */

@Component
public class UploadFileValidator {

	@Autowired
	private PartnerDao partnerDao;

	@Autowired
	private SettingsUtil settingsUtil;

	public FileUploadValidateResponse validateRows(List<Row> originalRows, List<Row> mandatoryFailedRows,
			String programId) {
		// if all rows are invalid
		if ((originalRows.size() - 1) == mandatoryFailedRows.size()) {
			List<ReferenceIdData> failedRecords = new ArrayList<ReferenceIdData>(mandatoryFailedRows.size());
			mandatoryFailedRows.stream().forEach(error -> {
				failedRecords.add(new ReferenceIdData(error.getRowNumber(), 
						 error.getError()));
			});
			return new FileUploadValidateResponse(null, failedRecords, null);
		}

		Map<String, List<String>> partnerMap = partnerDao.getPartnerDetails();
		List<ReferenceIdData> refIdRowList = new ArrayList<ReferenceIdData>();
		List<ReferenceIdData> readyToRowList = new ArrayList<ReferenceIdData>();
		// failed rows
		List<ReferenceIdData> failedRows = findAmountExceedRows(originalRows, mandatoryFailedRows, partnerMap,
				refIdRowList, programId);
		failedRows = findDupOrderIdRows(refIdRowList, failedRows);
		List<ReferenceIdData> noRefIdRows = findNoRefIdRows(refIdRowList, readyToRowList, partnerMap, programId);

		FileUploadValidateResponse resp = new FileUploadValidateResponse(noRefIdRows, failedRows,
				readyToRowList.size());
		return resp;
	}

	private List<ReferenceIdData> findAmountExceedRows(List<Row> originalRows, List<Row> mandatoryFailedRows,
			Map<String, List<String>> partnerMap, List<ReferenceIdData> refIdRowList, String programId) {
		List<ReferenceIdData> failedRowList = new ArrayList<ReferenceIdData>();

		// To set the reasons for mandatory field validation
		mandatoryFailedRows.stream().forEach(r -> {
			List<Object> values = r.getColumnValues();
			ReferenceIdData details = new ReferenceIdData(r.getRowNumber(), convertToString(values.get(0)),
					convertToString(values.get(1)), convertToString(values.get(2)), convertToString(values.get(3)),
					convertToString(values.get(4)), convert(convertToString(values.get(5)), Double.class), 1,
					r.getError());
			failedRowList.add(details);
		});

		// To find failed records - removing the mandatory field check failed records
		// from the original list
		List<Row> differneces = originalRows.stream().skip(1).filter(e -> !mandatoryFailedRows.contains(e))
				.collect(Collectors.toList());
		// This is to get the order ids if they are already exist
		List<String> existingIds = checkOrderIdAlreadyExist(differneces, failedRowList, programId);
		differneces.stream().forEach(r -> {
			List<Object> values = r.getColumnValues();
			ReferenceIdData details = new ReferenceIdData(r.getRowNumber(), convertToString(values.get(0)),
					convertToString(values.get(1)), convertToString(values.get(2)), convertToString(values.get(3)),
					convertToString(values.get(4)), convert(convertToString(values.get(5)), Double.class), 1,
					r.getError());
			if (existingIds.contains(details.getOrderId())) {
				details.setReason(settingsUtil.getValue(UploadFileConstants.ORDER_ID_EXIST));
				failedRowList.add(details);
				return;
			}
			if (Double.valueOf(settingsUtil.getValue(UploadFileConstants.ORD_AMT)) < details.getTotalCostOfOrders()) {
				details.setReason(settingsUtil.getValue(UploadFileConstants.AMT_EXCEED_KEY));
				failedRowList.add(details);
				return;
			}
			refIdRowList.add(details);

		});
		return failedRowList;
	}

	private List<ReferenceIdData> findDupOrderIdRows(List<ReferenceIdData> refIdRowList,
			List<ReferenceIdData> failedRows) {
		Set<String> nameSet = new HashSet<>();
		// To find the duplicate rows
		List<ReferenceIdData> dupRows = refIdRowList.stream().filter(e -> !nameSet.add(e.getOrderId()))
				.collect(Collectors.toList());
		// To set the error msg
		dupRows.stream().forEach(r -> {
			refIdRowList.remove(r);
			r.setReason(settingsUtil.getValue(UploadFileConstants.DUP_ORDER_ID_KEY));
			failedRows.add(r);
		});
		return failedRows;
	}

	private List<ReferenceIdData> findNoRefIdRows(List<ReferenceIdData> refIdRowList,
			List<ReferenceIdData> readyToRowList, Map<String, List<String>> partnerMap, String programId) {
		List<ReferenceIdData> noRefIdRowList = new ArrayList<ReferenceIdData>();
		refIdRowList.stream().forEach(s -> {
			if (isProcessWithoutRefId(String.valueOf(s.getMerContId()), programId, partnerMap)) {
				s.setReason(settingsUtil.getValue(UploadFileConstants.NO_REF_ID));
				noRefIdRowList.add(s);
			}
		});

		// This is to get the count same merchant or contract id. Count the cost of
		// orders and order id.
		Map<String, ReferenceIdData> map = noRefIdRowList.stream()
				.collect(Collectors.toMap(ReferenceIdData::getMerContId, Function.identity(), (a1, a2) -> {
					a1.joinCost(a2);
					return a1;
				}));
		// This is to get the good rows are ready to process
		refIdRowList.stream().filter(s -> !StringUtil.isValid(s.getReason())).forEach(r -> {
			readyToRowList.add(r);
		});

		return map.values().stream().collect(Collectors.toList());
	}

	private boolean isProcessWithoutRefId(String merchantContId, String programName,
			Map<String, List<String>> partnerMap) {
		return !partnerMap.get(programName).contains(merchantContId);
	}

	@SuppressWarnings("unchecked")
	public BulkImportValidateVo validateHeaderNames(List<Row> rows, String programId) {
		BulkImportValidateVo vo = new BulkImportValidateVo();
		List<String> headers = rows.get(0).getColumnValues().stream().map(v -> v.toString())
				.collect(Collectors.toList());
		String headerNames = settingsUtil.getValue("header_names");
		List<Map<String, Object>> headerList = JsonUtil.readValue(headerNames,
				new TypeReference<List<Map<String, Object>>>() {
				});
		List<ReferenceIdData> list = new ArrayList<ReferenceIdData>();

		headerList.stream().forEach(map -> {
			map.entrySet().stream().filter(v -> v.getKey().equalsIgnoreCase(programId)).forEach(val -> {
				List<String> dbHeaders = (List<String>) val.getValue();
				if (headers.size() != dbHeaders.size()) {
					list.add(new ReferenceIdData("Header names count mismatch"));
					return;
				}
				List<String> differneces = dbHeaders.stream().filter(e -> !headers.contains(e))
						.collect(Collectors.toList());
				StringBuilder reason = new StringBuilder();
				if (differneces.size() > 0) {
					reason.append("Header names ");
					differneces.stream().forEach(header -> {
						reason.append(header);
					});
					reason.append(" mismatch ");
					list.add(new ReferenceIdData(reason.toString()));
					return;
				}
			});

		});
		vo.setFailedRecords(list);
		return vo;
	}

	@SuppressWarnings("unchecked")
	private List<String> checkOrderIdAlreadyExist(List<Row> differneces, List<ReferenceIdData> failedRowList,
			String programId) {
		List<String> idList = new ArrayList<String>();
		differneces.stream().forEach(r -> {
			List<Object> values = r.getColumnValues();
			idList.add(convertToString(values.get(3)));
		});
		if (idList.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return partnerDao.checkOrderIdAlreadyExist(idList, programId);
	}

}
