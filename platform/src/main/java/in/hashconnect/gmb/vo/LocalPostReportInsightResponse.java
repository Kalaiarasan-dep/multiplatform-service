package in.hashconnect.gmb.vo;

import java.util.List;

public class LocalPostReportInsightResponse {
	private String name;
	private List<LocalPostMetrics> localPostMetrics;
	private String timeZone;
	private GoogleError error;
	private String status;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<LocalPostMetrics> getLocalPostMetrics() {
		return localPostMetrics;
	}
	public void setLocalPostMetrics(List<LocalPostMetrics> localPostMetrics) {
		this.localPostMetrics = localPostMetrics;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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
