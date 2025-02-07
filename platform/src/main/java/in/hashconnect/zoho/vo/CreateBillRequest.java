package in.hashconnect.zoho.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateBillRequest {
	@JsonIgnore
	private String billId;
	
	@JsonProperty("vendor_id")
	private String vendorId;
	@JsonProperty("branch_id")
	private String branchId;
	@JsonProperty("bill_number")
	private String billNumber;
	@JsonProperty("source_of_supply")
	private String sourceOfSupply;
	@JsonProperty("destination_of_supply")
	private String destinationOfSupply;
	@JsonProperty("gst_treatment")
	private String gstTreatment;
	@JsonProperty("tax_treatment")
	private String taxTreatment;
	@JsonProperty("gst_no")
	private String gstNo;
	private String date;
	@JsonProperty("due_date")
	private String dueDate;
	@JsonProperty("tds_tax_id")
	private String tdsTaxId;
	private List<BillLineItem> line_items;
	private String notes;
	@JsonProperty("is_inclusive_tax")
	private Boolean inclusiveTax;
	@JsonProperty("custom_fields")
	private List<CustomField> customFields;
	
	//update internal bill status
	@JsonIgnore
	private String billStatus;

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getSourceOfSupply() {
		return sourceOfSupply;
	}

	public void setSourceOfSupply(String sourceOfSupply) {
		this.sourceOfSupply = sourceOfSupply;
	}

	public String getDestinationOfSupply() {
		return destinationOfSupply;
	}

	public void setDestinationOfSupply(String destinationOfSupply) {
		this.destinationOfSupply = destinationOfSupply;
	}

	public String getGstTreatment() {
		return gstTreatment;
	}

	public void setGstTreatment(String gstTreatment) {
		this.gstTreatment = gstTreatment;
	}

	public String getTaxTreatment() {
		return taxTreatment;
	}

	public void setTaxTreatment(String taxTreatment) {
		this.taxTreatment = taxTreatment;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTdsTaxId() {
		return tdsTaxId;
	}

	public void setTdsTaxId(String tdsTaxId) {
		this.tdsTaxId = tdsTaxId;
	}

	public List<BillLineItem> getLine_items() {
		return line_items;
	}

	public void setLine_items(List<BillLineItem> line_items) {
		this.line_items = line_items;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean isInclusiveTax() {
		return inclusiveTax;
	}

	public void setInclusiveTax(Boolean inclusiveTax) {
		this.inclusiveTax = inclusiveTax;
	}

	public List<CustomField> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomField> customFields) {
		this.customFields = customFields;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}
}
