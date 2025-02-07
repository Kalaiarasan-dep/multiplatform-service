package in.hashconnect.gmb.vo;

import java.util.List;
import java.util.Map;

public class GoogleError {

	private Integer code;
	private String message;
	private String status;
	private List<Map<String, Object>> details;

	public List<Map<String, Object>> getDetails() {
		return details;
	}

	public void setDetails(List<Map<String, Object>> details) {
		this.details = details;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "GoogleError [code=" + code + ", message=" + message + ", status=" + status + ", details=" + details
				+ "]";
	}
}
