package in.hashconnect.excel.reader.exception;

public class ExcelReaderException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private int row;

	public ExcelReaderException() {
		super();
	}

	public ExcelReaderException(String message) {
		super(message);
	}

	public ExcelReaderException(int row, String message) {
		super(message);
		this.row = row;
	}

	public int getRow() {
		return row;
	}

}
