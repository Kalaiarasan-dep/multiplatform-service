package in.hashconnect.gmb.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleCaptchaVerifierVO {
	private String success;
	@JsonProperty(value = "error-codes")
	private List<String> errorCodes;
	private String hostname;
	@JsonProperty(value = "challenge_ts")
	private String challengeTimeStamp;

	public List<String> getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	@Override
	public String toString() {
		return "GoogleCaptchaVerifierVO [success=" + success + ", errorCodes="
				+ errorCodes + ", hostname=" + hostname
				+ ", challengeTimeStamp=" + challengeTimeStamp + "]";
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
}
