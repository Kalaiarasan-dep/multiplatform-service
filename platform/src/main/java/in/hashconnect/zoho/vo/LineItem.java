package in.hashconnect.zoho.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LineItem {
	private String item_id;
	private Integer item_order;
	private String rate;
	private String name = "";
	private String header_name;
	private Integer quantity;
	private String product_type;
	private String hsn_or_sac;
	@JsonIgnore
	private String originalRate;
	@JsonIgnore
	private String serialNo;
	@JsonIgnore
	private String splitAmt;

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public Integer getItem_order() {
		return item_order;
	}

	public void setItem_order(Integer item_order) {
		this.item_order = item_order;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeader_name() {
		return header_name;
	}

	public void setHeader_name(String header_name) {
		this.header_name = header_name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getHsn_or_sac() {
		return hsn_or_sac;
	}

	public void setHsn_or_sac(String hsn_or_sac) {
		this.hsn_or_sac = hsn_or_sac;
	}

	public String getOriginalRate() {
		return originalRate;
	}

	public void setOriginalRate(String originalRate) {
		this.originalRate = originalRate;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getSplitAmt() {
		return splitAmt;
	}

	public void setSplitAmt(String splitAmt) {
		this.splitAmt = splitAmt;
	}

}
