package in.hashconnect.zoho.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditNote {

	@JsonProperty("creditnote_id")
	private String creditNoteId;
	@JsonProperty("amount_applied")
	private String amountApplied;

	public String getCreditNoteId() {
		return creditNoteId;
	}

	public void setCreditNoteId(String creditNoteId) {
		this.creditNoteId = creditNoteId;
	}

	public String getAmountApplied() {
		return amountApplied;
	}

	public void setAmountApplied(String amountApplied) {
		this.amountApplied = amountApplied;
	}

	@Override
	public String toString() {
		return "CreditNote [creditNoteId=" + creditNoteId + ", amountApplied=" + amountApplied + "]";
	}

}
