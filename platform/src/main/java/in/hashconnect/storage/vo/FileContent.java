package in.hashconnect.storage.vo;

import java.io.InputStream;
import java.util.Date;

public class FileContent {

	private String bucket;
	private String folder;
	private String name;

	private byte[] data;
	private InputStream inStream;
	private Long length;

	private String contentType;
	private Date expirationTime;

	private Long rangeStart;
	private Long rangeEnd;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Long getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(Long rangeStart) {
		this.rangeStart = rangeStart;
	}

	public Long getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(Long rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
}
