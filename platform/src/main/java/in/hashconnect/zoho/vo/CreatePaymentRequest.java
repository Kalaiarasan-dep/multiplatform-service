package in.hashconnect.zoho.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CreatePaymentRequest {
	@JsonIgnore
	private Long advReqId;
	@JsonIgnore
	private Long payId;
	@JsonIgnore
	private Long invoiceId;
	@JsonIgnore
	private Long updatePaymentId;
	
	private String payment_id;
	private String customer_id;
	private String payment_mode;
	private String amount;
	private String date;
	private Long tax_id;
	private String payment_type;
	private Boolean is_advance_payment;
	private String reference_number;
	private String description;
	private String branch_id;
	private List<Invoice> invoices;
	private String customer_advance_account_id;
	private String account_id;
	private List<CustomField> custom_fields;

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getPayment_mode() {
		return payment_mode;
	}

	public void setPayment_mode(String payment_mode) {
		this.payment_mode = payment_mode;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}

	public List<Invoice> getInvoices() {
		if (invoices == null)
			invoices = new ArrayList<Invoice>();
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public Long getPayId() {
		return payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}

	public Boolean isIs_advance_payment() {
		return is_advance_payment;
	}

	public void setIs_advance_payment(Boolean is_advance_payment) {
		this.is_advance_payment = is_advance_payment;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public Long getTax_id() {
		return tax_id;
	}

	public void setTax_id(Long tax_id) {
		this.tax_id = tax_id;
	}

	public String getReference_number() {
		return reference_number;
	}

	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	public String getBranch_id() {
		return branch_id;
	}

	public void setBranch_id(String branch_id) {
		this.branch_id = branch_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getCustomer_advance_account_id() {
		return customer_advance_account_id;
	}

	public void setCustomer_advance_account_id(String customer_advance_account_id) {
		this.customer_advance_account_id = customer_advance_account_id;
	}

	public Long getAdvReqId() {
		return advReqId;
	}

	public void setAdvReqId(Long advReqId) {
		this.advReqId = advReqId;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public Long getUpdatePaymentId() {
		return updatePaymentId;
	}

	public void setUpdatePaymentId(Long updatePaymentId) {
		this.updatePaymentId = updatePaymentId;
	}

	public List<CustomField> getCustom_fields() {
		return custom_fields;
	}

	public void setCustom_fields(List<CustomField> custom_fields) {
		this.custom_fields = custom_fields;
	}

}
