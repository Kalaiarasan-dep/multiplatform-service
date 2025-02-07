package in.hashconnect.otp.vo;

public class Response {
	public enum STATUS {
		SUCCESS, INVALID_REQUEST, INVALID_OTP, RATELIMIT_EXCEEDED;
	};

	private STATUS status;
	private String otp;
	private Boolean valid;
	private String refId;
	private String to;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
