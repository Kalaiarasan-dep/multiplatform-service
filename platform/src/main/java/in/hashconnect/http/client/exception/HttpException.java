package in.hashconnect.http.client.exception;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class HttpException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String errorContent = null;

	public HttpException() {
		super();
	}

	public HttpException(InputStream in, Throwable t) {
		super(t);
		if (in != null) {
			try {
				errorContent = IOUtils.toString(in);
			} catch (IOException e) {
			}
		}
	}

	public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}

	public String getErrorContent() {
		return errorContent;
	}
}
