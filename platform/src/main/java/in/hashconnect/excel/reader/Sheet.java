package in.hashconnect.excel.reader;

import java.util.ArrayList;
import java.util.List;

public class Sheet {
	private List<Row> rows;

	public List<Row> getRows() {
		if(rows == null)
			rows = new ArrayList<>();
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	
}
