package in.hashconnect.excel.reader;

import java.util.List;

public class Row {
	private List<Object> columnValues;
	private boolean valid = true;
	private boolean header;
	private String error;
	private Integer rowNumber;

	public List<Object> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(List<Object> columnValues) {
		this.columnValues = columnValues;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

}
