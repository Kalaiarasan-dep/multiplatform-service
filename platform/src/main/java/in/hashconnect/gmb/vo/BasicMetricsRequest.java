package in.hashconnect.gmb.vo;

import java.util.List;

public class BasicMetricsRequest {
	
	private List<MetricRequest> metricRequests;
	private TimeRange timeRange;
	
	public List<MetricRequest> getMetricRequests() {
		return metricRequests;
	}
	public void setMetricRequests(List<MetricRequest> metricRequests) {
		this.metricRequests = metricRequests;
	}
	public TimeRange getTimeRange() {
		return timeRange;
	}
	public void setTimeRange(TimeRange timeRange) {
		this.timeRange = timeRange;
	}

}
