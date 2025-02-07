package in.hashconnect.gmb.vo;

import java.util.ArrayList;
import java.util.List;

public class ParamForBatchGet {
	private List<String> ranges;
	private Dimension majorDimension;
	
	public List<String> getRanges() {
		if(ranges == null)
			ranges = new ArrayList<String>();
		return ranges;
	}
	public void setRanges(List<String> ranges) {
		this.ranges = ranges;
	}
	public Dimension getMajorDimension() {
		return majorDimension;
	}
	public void setMajorDimension(Dimension majorDimension) {
		this.majorDimension = majorDimension;
	}

}
