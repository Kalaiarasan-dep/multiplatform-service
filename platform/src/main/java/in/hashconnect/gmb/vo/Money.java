package in.hashconnect.gmb.vo;

public class Money {

	private String currencyCode;
	private String units;
	private String nanos;

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getNanos() {
		return nanos;
	}

	public void setNanos(String nanos) {
		this.nanos = nanos;
	}
}
