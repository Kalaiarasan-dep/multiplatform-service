package in.hashconnect.api.vo;

import java.util.List;

public class Response<T> {
	public enum STATUS {
		FAILED, SUCCESS, DELAYED;
	}

	private STATUS status;
	private String desc;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private Integer code;
	private String message;

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}

	private List<T> records;
	private T data;

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <K> Response<K> ok() {
		Response<K> r = new Response<K>();
		r.setStatus(STATUS.SUCCESS);
		return r;
	}

	public static <K> Response<K> failed(String desc) {
		Response<K> r = new Response<K>();
		r.setStatus(STATUS.FAILED);
		r.setDesc(desc);
		return r;
	}

	public static <K> Response<K> delayed(String desc) {
		Response<K> r = new Response<K>();
		r.setStatus(STATUS.DELAYED);
		r.setDesc(desc);
		return r;
	}
}
