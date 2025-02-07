package in.hashconnect.zoho.vo;

public class Invoice {

	private String invoice_id;
	private String amount_applied;
	private String tax_id;

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getAmount_applied() {
		return amount_applied;
	}

	public void setAmount_applied(String amount_applied) {
		this.amount_applied = amount_applied;
	}

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}
}
