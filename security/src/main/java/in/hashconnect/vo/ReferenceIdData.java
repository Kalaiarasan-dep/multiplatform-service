package in.hashconnect.vo;

public class ReferenceIdData {
	private String programName;
	private String merContId;
	private String partnerName;
	private String orderId;
	private String orderDate;
	private Double totalCostOfOrders;
	private double noOfOrders;
	private String reason;
	private Integer recordNo;

	public ReferenceIdData(Integer recordNo, String programName, String merContId, String partnerName, String orderId,
			String orderDate, Double totalCostOfOrders, double noOfOrders, String reason) {
		this.recordNo = recordNo;
		this.programName = programName;
		this.merContId = merContId;
		this.partnerName = partnerName;
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.totalCostOfOrders = totalCostOfOrders;
		this.noOfOrders = noOfOrders;
		this.reason = reason;
	}

	public ReferenceIdData(Integer recordNo, String reason) {
		this.recordNo = recordNo;
		this.reason = reason;
	}
	public ReferenceIdData(String reason) {
		this.reason = reason;
	}

	public ReferenceIdData joinCost(ReferenceIdData batchDetails) {
		this.totalCostOfOrders += batchDetails.totalCostOfOrders;
		noOfOrders++;
		return this;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getMerContId() {
		return merContId;
	}

	public void setMerContId(String merContId) {
		this.merContId = merContId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public double getNoOfOrders() {
		return noOfOrders;
	}

	public void setNoOfOrders(double noOfOrders) {
		this.noOfOrders = noOfOrders;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(Integer recordNo) {
		this.recordNo = recordNo;
	}

	public Double getTotalCostOfOrders() {
		return totalCostOfOrders;
	}

	public void setTotalCostOfOrders(Double totalCostOfOrders) {
		this.totalCostOfOrders = totalCostOfOrders;
	}

}
