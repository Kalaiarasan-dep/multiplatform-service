package in.hashconnect.zoho.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CreateCustomerRequest {
	private String contact_name;
	private String contact_type;
	private String customer_sub_type;
	private String gst_treatment;
	private String gst_no;
	private String company_name;
	private String place_of_contact;
	private List<ContactPerson> contact_persons;

	private BillingAddress billing_address;
	private BillingAddress shipping_address;
	private String branch_id;
	// additional details for processing
	@JsonIgnore
	private String mode;
	@JsonIgnore
	private String amount;
	@JsonIgnore
	private Long payResponseId;
	@JsonIgnore
	private Long advReqId;
	@JsonIgnore
	private Long customerId;
	@JsonIgnore
	private Long regId;
	@JsonIgnore
	private String customer_id;

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public List<ContactPerson> getContact_persons() {
		return contact_persons;
	}

	public void setContact_persons(List<ContactPerson> contact_persons) {
		this.contact_persons = contact_persons;
	}

	public String getContact_type() {
		return contact_type;
	}

	public void setContact_type(String contact_type) {
		this.contact_type = contact_type;
	}

	public String getCustomer_sub_type() {
		return customer_sub_type;
	}

	public void setCustomer_sub_type(String customer_sub_type) {
		this.customer_sub_type = customer_sub_type;
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

	public String getPlace_of_contact() {
		return place_of_contact;
	}

	public void setPlace_of_contact(String place_of_contact) {
		this.place_of_contact = place_of_contact;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Long getPayResponseId() {
		return payResponseId;
	}

	public void setPayResponseId(Long payResponseId) {
		this.payResponseId = payResponseId;
	}

	public BillingAddress getBilling_address() {
		return billing_address;
	}

	public void setBilling_address(BillingAddress billing_address) {
		this.billing_address = billing_address;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getAdvReqId() {
		return advReqId;
	}

	public void setAdvReqId(Long advReqId) {
		this.advReqId = advReqId;
	}

	public BillingAddress getShipping_address() {
		return shipping_address;
	}

	public void setShipping_address(BillingAddress shipping_address) {
		this.shipping_address = shipping_address;
	}

	public Long getRegId() {
		return regId;
	}

	public void setRegId(Long regId) {
		this.regId = regId;
	}

	public String getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(String branch_id) {
		this.branch_id = branch_id;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

}
