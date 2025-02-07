package in.hashconnect.api.vo;

import java.util.List;

public class PaginationResponse<T> extends Response<T> {
	private Integer totalRecords;
	private List<T> records;

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}
}
