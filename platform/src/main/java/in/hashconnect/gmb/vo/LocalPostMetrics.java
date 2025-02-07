package in.hashconnect.gmb.vo;

import java.util.List;

public class LocalPostMetrics {
private String localPostName;
private List<MetricValue> metricValues;

public String getLocalPostName() {
	return localPostName;
}
public void setLocalPostName(String localPostName) {
	this.localPostName = localPostName;
}
public List<MetricValue> getMetricValues() {
	return metricValues;
}
public void setMetricValues(List<MetricValue> metricValues) {
	this.metricValues = metricValues;
}	
}
