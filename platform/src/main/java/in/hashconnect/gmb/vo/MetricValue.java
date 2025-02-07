package in.hashconnect.gmb.vo;

public class MetricValue {
	
	private  Metric metric;
	private DimensionalMetricValue totalValue;
	private DimensionalMetricValue dimensionalValues;
	
	public Metric getMetric() {
		return metric;
	}
	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	public DimensionalMetricValue getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(DimensionalMetricValue totalValue) {
		this.totalValue = totalValue;
	}
	public DimensionalMetricValue getDimensionalValues() {
		return dimensionalValues;
	}
	public void setDimensionalValues(DimensionalMetricValue dimensionalValues) {
		this.dimensionalValues = dimensionalValues;
	}
	

}
