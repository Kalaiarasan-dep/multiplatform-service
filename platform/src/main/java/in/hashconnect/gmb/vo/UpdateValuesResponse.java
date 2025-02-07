package in.hashconnect.gmb.vo;

public class UpdateValuesResponse {
	private String spreadsheetId;
	private String updatedRange;
	private Integer updatedRows;
	private Integer updatedColumns;
	private Integer updatedCells;
	private ValueRange updatedData;
	private GoogleError error;
	private String status;
	
	public String getSpreadsheetId() {
		return spreadsheetId;
	}
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}
	public String getUpdatedRange() {
		return updatedRange;
	}
	public void setUpdatedRange(String updatedRange) {
		this.updatedRange = updatedRange;
	}
	public Integer getUpdatedRows() {
		return updatedRows;
	}
	public void setUpdatedRows(Integer updatedRows) {
		this.updatedRows = updatedRows;
	}
	public Integer getUpdatedColumns() {
		return updatedColumns;
	}
	public void setUpdatedColumns(Integer updatedColumns) {
		this.updatedColumns = updatedColumns;
	}
	public Integer getUpdatedCells() {
		return updatedCells;
	}
	public void setUpdatedCells(Integer updatedCells) {
		this.updatedCells = updatedCells;
	}
	public ValueRange getUpdatedData() {
		return updatedData;
	}
	public void setUpdatedData(ValueRange updatedData) {
		this.updatedData = updatedData;
	}
	public GoogleError getError() {
		return error;
	}
	public void setError(GoogleError error) {
		this.error = error;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
