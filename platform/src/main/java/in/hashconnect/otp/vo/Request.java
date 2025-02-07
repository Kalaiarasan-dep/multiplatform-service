package in.hashconnect.otp.vo;

import java.sql.Timestamp;

public class Request {
	private String to;
	private String ip;
	private String refId;
	private String otp;
	private Timestamp expiryTime;

	public static Request create() {
		return new Request();
	}

	public String getTo() {
		return to;
	}

	public Request to(String to) {
		this.to = to;
		return this;
	}

	public String getRefId() {
		return refId;
	}

	public Request refId(String refId) {
		this.refId = refId;
		return this;
	}

	public String getOtp() {
		return otp;
	}

	public Request otp(String otp) {
		this.otp = otp;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public Request ip(String ip) {
		this.ip = ip;
		return this;
	}

	public Timestamp getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Timestamp expiryTime) {
		this.expiryTime = expiryTime;
	}

}
