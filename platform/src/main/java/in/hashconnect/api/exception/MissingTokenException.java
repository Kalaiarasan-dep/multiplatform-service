package in.hashconnect.api.exception;

public class MissingTokenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MissingTokenException(String msg) {
		super(msg);
	}

}
