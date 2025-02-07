package in.hashconnect.vo;

import java.util.List;
import java.util.Map;

public class BulkImportVo {

	private Integer refId;
	private List<String> uploadedColumns;
	private List<Map<String, Object>> mappedFields;

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public List<String> getUploadedColumns() {
		return uploadedColumns;
	}

	public void setUploadedColumns(List<String> uploadedColumns) {
		this.uploadedColumns = uploadedColumns;
	}

	public List<Map<String, Object>> getMappedFields() {
		return mappedFields;
	}

	public void setMappedFields(List<Map<String, Object>> mappedFields) {
		this.mappedFields = mappedFields;
	}
}
