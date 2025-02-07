package in.hashconnect.gmb.vo;

import java.util.List;

public class GoogleSheetBatchUpdateResponse {
	private String spreadsheetId;
	private Integer totalUpdatedRows;
	private Integer totalUpdatedColumns;
	private Integer totalUpdatedCells;
	private Integer totalUpdatedSheets;
	List<UpdateValuesResponse>responses;
	private GoogleError error;
	private String status;
	
	public String getSpreadsheetId() {
		return spreadsheetId;
	}
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}
	public Integer getTotalUpdatedRows() {
		return totalUpdatedRows;
	}
	public void setTotalUpdatedRows(Integer totalUpdatedRows) {
		this.totalUpdatedRows = totalUpdatedRows;
	}
	public Integer getTotalUpdatedColumns() {
		return totalUpdatedColumns;
	}
	public void setTotalUpdatedColumns(Integer totalUpdatedColumns) {
		this.totalUpdatedColumns = totalUpdatedColumns;
	}
	public Integer getTotalUpdatedCells() {
		return totalUpdatedCells;
	}
	public void setTotalUpdatedCells(Integer totalUpdatedCells) {
		this.totalUpdatedCells = totalUpdatedCells;
	}
	public Integer getTotalUpdatedSheets() {
		return totalUpdatedSheets;
	}
	public void setTotalUpdatedSheets(Integer totalUpdatedSheets) {
		this.totalUpdatedSheets = totalUpdatedSheets;
	}
	public List<UpdateValuesResponse> getResponses() {
		return responses;
	}
	public void setResponses(List<UpdateValuesResponse> responses) {
		this.responses = responses;
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
