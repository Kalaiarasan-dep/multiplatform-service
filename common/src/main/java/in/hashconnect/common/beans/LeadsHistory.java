package in.hashconnect.common.beans;

import in.hashconnect.common.util.TimeUtilCommon;

public class LeadsHistory {
	private Long id;
	private Long leadId;
	private Long storeMapId;
	private int statusId;
	private String description;
	private String createdDate;
	private String notInterestedReason;
	private Long storeId;
	private Integer sourceId;
	private String comments;

	public LeadsHistory() {
	}

	public LeadsHistory(long lead_id, long lead_store_map_id, int status_id, String description, String created_date,
			String not_interested_reason, Long store_id, Integer source_id) {
		super();
		this.leadId = lead_id;
		this.storeMapId = lead_store_map_id;
		this.statusId = status_id;
		this.description = description;
		this.createdDate = created_date;
		this.notInterestedReason = not_interested_reason;
		this.storeId = store_id;
		this.sourceId = source_id;
	
	}

	public Long getLeadId() {
		return leadId;
	}

	public Long getStoreMapId() {
		return storeMapId;
	}

	public LeadsHistory setLeadId(long lead_id) {
		this.leadId = lead_id;
		return this;
	}

	public LeadsHistory setStoreMapId(long lead_store_map_id) {
		this.storeMapId = lead_store_map_id;
		return this;
	}

	public LeadsHistory setStatusId(int status_id) {
		this.statusId = status_id;
		return this;
	}

	public LeadsHistory setCreatedDate(String created_date) {
		this.createdDate = created_date;
		return this;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getDescription() {
		return description;
	}

	public LeadsHistory setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getNotInterestedReason() {
		return notInterestedReason;
	}

	public LeadsHistory setNotInterestedReason(String not_interested_reason) {
		this.notInterestedReason = not_interested_reason;
		return this;
	}

	public Long getStoreId() {
		return storeId;
	}

	public LeadsHistory setStoreId(long store_id) {
		this.storeId = store_id;
		return this;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public LeadsHistory setSourceId(Integer source_id) {
		this.sourceId = source_id;
		return this;
	}

	public LeadsHistory setStoreId(Long store_id) {
		this.storeId = store_id;
		return this;
	}

	public static LeadsHistory create() {
		return new LeadsHistory().setCreatedDate(TimeUtilCommon.getCurrentTimeStampInString());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public LeadsHistory setComments(String comments) {
		this.comments = comments;
		return this;
	}

}
