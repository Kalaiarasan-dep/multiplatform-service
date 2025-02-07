package in.hashconnect.exceptions;

public class CustomException extends RuntimeException{
    private static final long serialVersionUID = 5278938262129289548L;
    private final String message;

    public CustomException(Throwable throwableCause) {
        super(throwableCause);
        this.message = "";
    }
    public CustomException(String message) {
        this.message = message;
    }
    public CustomException(String msg, Throwable throwableCause) {
        super(msg, throwableCause);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
