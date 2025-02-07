package in.hashconnect.zoho.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CreateInvoiceRequest {
	private String customer_id;
	private List<String> contact_persons;
	private String invoice_number;
	private String place_of_supply;
	private String gst_treatment;
	private String gst_no;
	private String reference_number;
	private String template_id;
	private String date;
	private Boolean is_inclusive_tax;
	private String exchange_rate;
	private List<LineItem> line_items;
	private String branch_id;
	private String subject_content;
	private String terms;
	private String reason;
	private String next_action;
	private String salesperson_name;
	private List<CustomField> custom_fields;
	// addtional
	@JsonIgnore
	private Long regOfferId;
	@JsonIgnore
	private Long dbCustomerId;
	@JsonIgnore
	private Long payId;
	@JsonIgnore
	private Long invoiceId;
	@JsonIgnore
	private Long advReqId;
	@JsonIgnore
	private String invoice_no;
	@JsonIgnore
	private Long updateInvoiceId;
	@JsonIgnore
	private Long creditNoteId;
	@JsonIgnore
	private boolean insertToDB = true;

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public List<String> getContact_persons() {
		return contact_persons;
	}

	public void setContact_persons(List<String> contact_persons) {
		this.contact_persons = contact_persons;
	}

	public String getInvoice_number() {
		return invoice_number;
	}

	public void setInvoice_number(String invoice_number) {
		this.invoice_number = invoice_number;
	}

	public String getPlace_of_supply() {
		return place_of_supply;
	}

	public void setPlace_of_supply(String place_of_supply) {
		this.place_of_supply = place_of_supply;
	}

	public String getGst_treatment() {
		return gst_treatment;
	}

	public void setGst_treatment(String gst_treatment) {
		this.gst_treatment = gst_treatment;
	}

	public String getGst_no() {
		return gst_no;
	}

	public void setGst_no(String gst_no) {
		this.gst_no = gst_no;
	}

	public String getReference_number() {
		return reference_number;
	}

	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	public String getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Boolean getIs_inclusive_tax() {
		return is_inclusive_tax;
	}

	public void setIs_inclusive_tax(Boolean is_inclusive_tax) {
		this.is_inclusive_tax = is_inclusive_tax;
	}

	public String getExchange_rate() {
		return exchange_rate;
	}

	public void setExchange_rate(String exchange_rate) {
		this.exchange_rate = exchange_rate;
	}

	public List<LineItem> getLine_items() {
		return line_items;
	}

	public void setLine_items(List<LineItem> line_items) {
		this.line_items = line_items;
	}

	public Long getRegOfferId() {
		return regOfferId;
	}

	public void setRegOfferId(Long regOfferId) {
		this.regOfferId = regOfferId;
	}

	public String getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(String branch_id) {
		this.branch_id = branch_id;
	}

	public Long getDbCustomerId() {
		return dbCustomerId;
	}

	public void setDbCustomerId(Long dbCustomerId) {
		this.dbCustomerId = dbCustomerId;
	}

	public Long getPayId() {
		return payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Long getAdvReqId() {
		return advReqId;
	}

	public void setAdvReqId(Long advReqId) {
		this.advReqId = advReqId;
	}

	public String getSubject_content() {
		return subject_content;
	}

	public void setSubject_content(String subject_content) {
		this.subject_content = subject_content;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public Long getUpdateInvoiceId() {
		return updateInvoiceId;
	}

	public void setUpdateInvoiceId(Long updateInvoiceId) {
		this.updateInvoiceId = updateInvoiceId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getCreditNoteId() {
		return creditNoteId;
	}

	public void setCreditNoteId(Long creditNoteId) {
		this.creditNoteId = creditNoteId;
	}

	public boolean isInsertToDB() {
		return insertToDB;
	}

	public void setInsertToDB(boolean insertToDB) {
		this.insertToDB = insertToDB;
	}

	public String getNext_action() {
		return next_action;
	}

	public void setNext_action(String next_action) {
		this.next_action = next_action;
	}

	public String getSalesperson_name() {
		return salesperson_name;
	}

	public void setSalesperson_name(String salesperson_name) {
		this.salesperson_name = salesperson_name;
	}

	public List<CustomField> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(List<CustomField> custom_fields) {
		this.custom_fields = custom_fields;
	}

}
