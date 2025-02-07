package in.hashconnect.zoho.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplyCreditNoteRequest {

	@JsonIgnore
	private String invoiceId;

	@JsonProperty("apply_creditnotes")
	private List<CreditNote> creditnotes;

	public List<CreditNote> getCreditnotes() {
		return creditnotes;
	}

	public void setCreditnotes(List<CreditNote> creditnotes) {
		this.creditnotes = creditnotes;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public String toString() {
		return "ApplyCreditNoteRequest [invoiceId=" + invoiceId + ", creditnotes=" + creditnotes + "]";
	}

}
