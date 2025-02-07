package in.hashconnect.zoho.exception;

import java.util.Map;

public class ZohoInvalidResponseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> response;

	public ZohoInvalidResponseException(Map<String, Object> response) {
		super();
		this.response = response;
	}

	public ZohoInvalidResponseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ZohoInvalidResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZohoInvalidResponseException(String message) {
		super(message);
	}

	public ZohoInvalidResponseException(Throwable cause) {
		super(cause);
	}
	
	public Map<String, Object> getResponse() {
		return this.response;
	}

}
