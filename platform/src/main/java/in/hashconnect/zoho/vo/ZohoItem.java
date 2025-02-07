package in.hashconnect.zoho.vo;

import java.util.List;

import in.hashconnect.util.JsonUtil;

public class ZohoItem {

	private String item_id;
	private String name;
	private Float rate;
	private String description;
	private String tax_id;
	private String tax_percentage;
	private String sku;
	private String product_type;
	private String hsn_or_sac;
	private Boolean is_taxable;
	private String tax_exemption_id;
	private String account_id;
	private String avatax_tax_code;
	private String avatax_use_code;
	private String item_type;
	private String purchase_description;
	private String purchase_rate;
	private String purchase_account_id;
	private String inventory_account_id;
	private String vendor_id;
	private String reorder_level;
	private String initial_stock;
	private String initial_stock_rate;
	private List<CustomField> custom_fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}

	public String getTax_percentage() {
		return tax_percentage;
	}

	public void setTax_percentage(String tax_percentage) {
		this.tax_percentage = tax_percentage;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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

	public Boolean getIs_taxable() {
		return is_taxable;
	}

	public void setIs_taxable(Boolean is_taxable) {
		this.is_taxable = is_taxable;
	}

	public String getTax_exemption_id() {
		return tax_exemption_id;
	}

	public void setTax_exemption_id(String tax_exemption_id) {
		this.tax_exemption_id = tax_exemption_id;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getAvatax_tax_code() {
		return avatax_tax_code;
	}

	public void setAvatax_tax_code(String avatax_tax_code) {
		this.avatax_tax_code = avatax_tax_code;
	}

	public String getAvatax_use_code() {
		return avatax_use_code;
	}

	public void setAvatax_use_code(String avatax_use_code) {
		this.avatax_use_code = avatax_use_code;
	}

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public String getPurchase_description() {
		return purchase_description;
	}

	public void setPurchase_description(String purchase_description) {
		this.purchase_description = purchase_description;
	}

	public String getPurchase_rate() {
		return purchase_rate;
	}

	public void setPurchase_rate(String purchase_rate) {
		this.purchase_rate = purchase_rate;
	}

	public String getPurchase_account_id() {
		return purchase_account_id;
	}

	public void setPurchase_account_id(String purchase_account_id) {
		this.purchase_account_id = purchase_account_id;
	}

	public String getInventory_account_id() {
		return inventory_account_id;
	}

	public void setInventory_account_id(String inventory_account_id) {
		this.inventory_account_id = inventory_account_id;
	}

	public String getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}

	public String getReorder_level() {
		return reorder_level;
	}

	public void setReorder_level(String reorder_level) {
		this.reorder_level = reorder_level;
	}

	public String getInitial_stock() {
		return initial_stock;
	}

	public void setInitial_stock(String initial_stock) {
		this.initial_stock = initial_stock;
	}

	public String getInitial_stock_rate() {
		return initial_stock_rate;
	}

	public void setInitial_stock_rate(String initial_stock_rate) {
		this.initial_stock_rate = initial_stock_rate;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public List<CustomField> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(List<CustomField> custom_fields) {
		this.custom_fields = custom_fields;
	}

	@Override
	public String toString() {
		return JsonUtil.toString(this);
	}
}
