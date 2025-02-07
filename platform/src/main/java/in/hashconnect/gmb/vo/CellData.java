package in.hashconnect.gmb.vo;

public class CellData {
	private ExtendedValue userEnteredValue;
	private ExtendedValue effectiveValue;
	private String hyperlink;
	
	public ExtendedValue getUserEnteredValue() {
		return userEnteredValue;
	}
	public void setUserEnteredValue(ExtendedValue userEnteredValue) {
		this.userEnteredValue = userEnteredValue;
	}
	public ExtendedValue getEffectiveValue() {
		return effectiveValue;
	}
	public void setEffectiveValue(ExtendedValue effectiveValue) {
		this.effectiveValue = effectiveValue;
	}
	public String getHyperlink() {
		return hyperlink;
	}
	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}
}
