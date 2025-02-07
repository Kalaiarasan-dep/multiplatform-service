package in.hashconnect.vo;

import java.util.List;
import java.util.Map;

import in.hashconnect.excel.reader.Row;

public class BulkImportValidateVo {

	private Integer refId;
	private List<Row> errors;
	private List<Map<String, Object>> unmappedFields;
	private Integer totalRecordsToImport;
	private List<ReferenceIdData> noReferencesList;
	private List<ReferenceIdData> failedRecords;
	
	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public List<Row> getErrors() {
		return errors;
	}

	public void setErrors(List<Row> errors) {
		this.errors = errors;
	}

	public List<Map<String, Object>> getUnmappedFields() {
		return unmappedFields;
	}

	public void setUnmappedFields(List<Map<String, Object>> unmappedFields) {
		this.unmappedFields = unmappedFields;
	}

	public Integer getTotalRecordsToImport() {
		return totalRecordsToImport;
	}

	public void setTotalRecordsToImport(Integer totalRecordsToImport) {
		this.totalRecordsToImport = totalRecordsToImport;
	}

	public List<ReferenceIdData> getNoReferencesList() {
		return noReferencesList;
	}

	public void setNoReferencesList(List<ReferenceIdData> noReferencesList) {
		this.noReferencesList = noReferencesList;
	}

	public List<ReferenceIdData> getFailedRecords() {
		return failedRecords;
	}

	public void setFailedRecords(List<ReferenceIdData> failedRecords) {
		this.failedRecords = failedRecords;
	}
}
