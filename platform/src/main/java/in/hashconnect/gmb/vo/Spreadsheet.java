package in.hashconnect.gmb.vo;

import java.util.ArrayList;
import java.util.List;

public class Spreadsheet {
	private String spreadsheetId;
	private String spreadsheetUrl;
	private SpreadsheetProperties properties;
	private List<Sheet> sheets;
	private GoogleError error;
	private String status;
	
	public String getSpreadsheetId() {
		return spreadsheetId;
	}
	public void setSpreadsheetId(String spreadsheetId) {
		this.spreadsheetId = spreadsheetId;
	}
	public String getSpreadsheetUrl() {
		return spreadsheetUrl;
	}
	public void setSpreadsheetUrl(String spreadsheetUrl) {
		this.spreadsheetUrl = spreadsheetUrl;
	}
	public SpreadsheetProperties getProperties() {
		if(properties == null)
			properties = new SpreadsheetProperties();
		return properties;
	}
	public void setProperties(SpreadsheetProperties properties) {
		this.properties = properties;
	}
	public List<Sheet> getSheets() {
		if(sheets == null)
			sheets = new ArrayList<Sheet>();
		return sheets;
	}
	public void setSheets(List<Sheet> sheets) {
		this.sheets = sheets;
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
