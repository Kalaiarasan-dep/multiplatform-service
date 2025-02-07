package in.hashconnect.gmb.vo;

import java.util.List;

public class LocalPostReportInsight {
	
	private List<String> localPostNames;
	private BasicMetricsRequest basicRequest;
	
	public List<String> getLocalPostNames() {
		return localPostNames;
	}
	public void setLocalPostNames(List<String> localPostNames) {
		this.localPostNames = localPostNames;
	}
	public BasicMetricsRequest getBasicRequest() {
		return basicRequest;
	}
	public void setBasicRequest(BasicMetricsRequest basicRequest) {
		this.basicRequest = basicRequest;
	}

}
