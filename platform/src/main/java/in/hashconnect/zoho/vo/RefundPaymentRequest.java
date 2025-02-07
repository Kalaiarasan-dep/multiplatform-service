package in.hashconnect.zoho.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RefundPaymentRequest {
	@JsonIgnore
	private String paymentId;
	private String from_account_id;
	private String amount;
	private String date;
	private String refund_mode;
	private String reference_number;
	private String exchange_number;
	private String customer_id;
	private String customer_payment_id;
	private Long refundRequestId;
	private String requestUrl;

	public String getFrom_account_id() {
		return from_account_id;
	}

	public void setFrom_account_id(String from_account_id) {
		this.from_account_id = from_account_id;
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

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getRefund_mode() {
		return refund_mode;
	}

	public void setRefund_mode(String refund_mode) {
		this.refund_mode = refund_mode;
	}

	public String getReference_number() {
		return reference_number;
	}

	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	public String getExchange_number() {
		return exchange_number;
	}

	public void setExchange_number(String exchange_number) {
		this.exchange_number = exchange_number;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_payment_id() {
		return customer_payment_id;
	}

	public void setCustomer_payment_id(String customer_payment_id) {
		this.customer_payment_id = customer_payment_id;
	}

	public Long getRefundRequestId() {
		return refundRequestId;
	}

	public void setRefundRequestId(Long refundRequestId) {
		this.refundRequestId = refundRequestId;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
