package in.hashconnect.gmb.vo;

public class DimensionalMetricValue {
	
	private MetricOption metricOption;
	private TimeDimension timeDimension;
	private String value;
	
	public MetricOption getMetricOption() {
		return metricOption;
	}
	public void setMetricOption(MetricOption metricOption) {
		this.metricOption = metricOption;
	}
	public TimeDimension getTimeDimension() {
		return timeDimension;
	}
	public void setTimeDimension(TimeDimension timeDimension) {
		this.timeDimension = timeDimension;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
