package in.hashconnect.common.beans;

public class LeadsStoreMapping {
	private Long id;
	private Long lead_id;
	private Long store_id;
	private Integer source_id;
	private Integer prod_id;
	private Integer status_id;
	private String created_date;
	private String modified_date;
	private LeadsHistory historyEntry;
	private Long bookaDemoRefId;
	private String bookaDemoInterested;

	public LeadsStoreMapping() {
		super();
	}

	public LeadsStoreMapping(long lead_id, Long store_id, Integer source_id, Integer prod_id, Integer status_id,
			String created_date, String modified_date) {
		super();
		this.lead_id = lead_id;
		this.store_id = store_id;
		this.source_id = source_id;
		this.prod_id = prod_id;
		this.status_id = status_id;
		this.created_date = created_date;
		this.modified_date = modified_date;
	}

	public Long getLead_id() {
		return lead_id;
	}

	public Long getStore_id() {
		return store_id;
	}

	public Integer getSource_id() {
		return source_id;
	}

	public Integer getProd_id() {
		return prod_id;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public String getCreated_date() {
		return created_date;
	}

	public String getModified_date() {
		return modified_date;
	}

	public LeadsStoreMapping setLead_id(long lead_id) {
		this.lead_id = lead_id;
		return this;
	}

	public LeadsStoreMapping setStore_id(long store_id) {
		this.store_id = store_id;
		return this;
	}

	public LeadsStoreMapping setSource_id(int source_id) {
		this.source_id = source_id;
		return this;
	}

	public LeadsStoreMapping setProd_id(int prod_id) {
		this.prod_id = prod_id;
		return this;
	}

	public LeadsStoreMapping setStatus_id(int status_id) {
		this.status_id = status_id;
		return this;
	}

	public LeadsStoreMapping setCreated_date(String created_date) {
		this.created_date = created_date;
		return this;
	}

	public LeadsStoreMapping setModified_date(String modified_date) {
		this.modified_date = modified_date;
		return this;
	}

	public LeadsHistory getHistoryEntry() {
		return historyEntry;
	}

	public LeadsStoreMapping setHistoryEntry(LeadsHistory historyEntry) {
		this.historyEntry = historyEntry;
		return this;
	}
	
	public static LeadsStoreMapping create() {
		return new LeadsStoreMapping();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBookaDemoRefId() {
		return bookaDemoRefId;
	}

	public void setBookaDemoRefId(Long bookaDemoRefId) {
		this.bookaDemoRefId = bookaDemoRefId;
	}

	public String getBookaDemoInterested() {
		return bookaDemoInterested;
	}

	public void setBookaDemoInterested(String bookaDemoInterested) {
		this.bookaDemoInterested = bookaDemoInterested;
	}

}
