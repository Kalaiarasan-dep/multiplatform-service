package in.hashconnect.gmb.vo;

import java.util.List;

public class GoogleSheetValuesResponse {
private String spreadsheetId;
private List<ValueRange> valueRanges;

public String getSpreadsheetId() {
	return spreadsheetId;
}
public void setSpreadsheetId(String spreadsheetId) {
	this.spreadsheetId = spreadsheetId;
}
public List<ValueRange> getValueRanges() {
	return valueRanges;
}
public void setValueRanges(List<ValueRange> valueRanges) {
	this.valueRanges = valueRanges;
}
@Override
public String toString() {
	return "GoogleSheetValuesResponse [spreadsheetId=" + spreadsheetId + ", valueRanges=" + valueRanges + "]";
}
}
