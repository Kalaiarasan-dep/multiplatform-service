package in.hashconnect.zoho.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillLineItem {
	@JsonProperty("item_id")
	private String itemId;
	private String quantity;
	private String rate;
	@JsonProperty("tax_id")
	private String taxId;
	@JsonProperty("project_id")
	private String projectId;
	@JsonProperty("customer_id")
	private String customerId;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
}
