package in.hashconnect.excel.reader;

import java.util.ArrayList;
import java.util.List;

public class WorkBook {
	private List<Sheet> sheets;

	public List<Sheet> getSheets() {
		if (sheets == null)
			sheets = new ArrayList<>();
		return sheets;
	}

	public void setSheets(List<Sheet> sheets) {
		this.sheets = sheets;
	}
}
