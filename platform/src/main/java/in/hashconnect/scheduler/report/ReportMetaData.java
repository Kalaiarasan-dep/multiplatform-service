package in.hashconnect.scheduler.report;

import java.util.List;

import in.hashconnect.excel.Column;

public class ReportMetaData {
	private List<Column> columns;
	private Integer rowCounter;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Integer getRowCounter() {
		if (rowCounter == null)
			return 1;
		return rowCounter;
	}

	public void setRowCounter(Integer rowCounter) {
		this.rowCounter = rowCounter;
	}
}
