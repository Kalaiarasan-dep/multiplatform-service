package in.hashconnect.notification.service.vo;

import java.io.InputStream;

public class Attachment {

	private InputStream data;
	private byte[] byteData;
	private String contentType;
	private String fileName;
	private String zipFile;

	public Attachment() {
		super();
	}

	public Attachment(InputStream data, String contentType, String fileName) {
		super();
		this.data = data;
		this.contentType = contentType;
		this.fileName = fileName;
	}

	public Attachment(byte[] data, String contentType, String fileName) {
		super();
		this.byteData = data;
		this.contentType = contentType;
		this.fileName = fileName;
	}

	public byte[] getByteData() {
		return byteData;
	}

	public InputStream getData() {
		return data;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public String getZipFile() {
		return zipFile;
	}

	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
