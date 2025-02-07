package in.hashconnect.gmb.vo;

import java.util.List;


public class MetricRequest {

	private Metric metric;
	private List<MetricOption> options;
	
	public Metric getMetric() {
		return metric;
	}
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	public List<MetricOption> getOptions() {
		return options;
	}
	public void setOptions(List<MetricOption> options) {
		this.options = options;
	}
	
}
