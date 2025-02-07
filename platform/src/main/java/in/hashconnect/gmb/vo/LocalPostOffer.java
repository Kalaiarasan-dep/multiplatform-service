package in.hashconnect.gmb.vo;

import static in.hashconnect.util.StringUtil.escapeHtml;

public class LocalPostOffer {

	private String couponCode;
	private String redeemOnlineUrl;
	private String termsConditions;

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode == null ? couponCode : escapeHtml(couponCode);
	}

	public String getRedeemOnlineUrl() {
		return redeemOnlineUrl;
	}

	public void setRedeemOnlineUrl(String redeemOnlineUrl) {
		this.redeemOnlineUrl = redeemOnlineUrl;
	}

	public String getTermsConditions() {
		return termsConditions;
	}

	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions == null ? termsConditions : escapeHtml(termsConditions);
	}

}
