package in.hashconnect.gmb.vo;

import java.util.ArrayList;
import java.util.List;

public class ValueRange {
	
	private String range;
	private Dimension majorDimension;
	private List<List<Object>> values;
	private GoogleError error;
	private String status;
	
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public Dimension getMajorDimension() {
		return majorDimension;
	}
	public void setMajorDimension(Dimension majorDimension) {
		this.majorDimension = majorDimension;
	}
	public List<List<Object>> getValues() {
		if(values == null)
			values = new ArrayList<List<Object>>();
		return values;
	}
	public void setValues(List<List<Object>> values) {
		this.values = values;
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
	@Override
	public String toString() {
		return "ValueRange [range=" + range + ", majorDimension=" + majorDimension + ", values=" + values + ", error="
				+ error + ", status=" + status + "]";
	}
	



}
