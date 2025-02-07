package in.hashconnect.gmb.vo;

import java.util.ArrayList;
import java.util.List;

public class BatchUpdateGoogleSheet {
	private ValueInputOption valueInputOption;
	private List<ValueRange> data;

	public ValueInputOption getValueInputOption() {
		return valueInputOption;
	}

	public void setValueInputOption(ValueInputOption valueInputOption) {
		this.valueInputOption = valueInputOption;
	}

	public List<ValueRange> getData() {
		if (data == null)
			data = new ArrayList<ValueRange>();
		return data;
	}

	public void setData(List<ValueRange> data) {
		this.data = data;
	}

}
