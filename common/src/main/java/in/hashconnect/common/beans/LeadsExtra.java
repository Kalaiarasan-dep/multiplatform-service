package in.hashconnect.common.beans;

public class LeadsExtra {
	private long lead_id;
	private int facebook_form_id;
	private String facebook_call_CID;
	private String remarks;
	private String fb_unique_lead_id;
	private String link_clicked;
	private String smsStatus;
	private String missed_call;
	private String units;
	private int call_center_update;
	private String cid;
	private String utm_source;
	private String utm_campaign;
	private String utm_medium;
	private String utm_content;
	private String created_date;
	private String modified_date;
	private  String days;
	public LeadsExtra(){
		super();
	}
	public LeadsExtra(long lead_id, int facebook_form_id, String facebook_call_CID, String remarks,
			String fb_unique_lead_id, String link_clicked, String smsStatus, String missed_call, String units,
			int call_center_update, String cid, String utm_source, String utm_campaign, String utm_medium,
			String utm_content, String created_date, String modified_date,String days) {
		super();
		this.lead_id = lead_id;
		this.facebook_form_id = facebook_form_id;
		this.facebook_call_CID = facebook_call_CID;
		this.remarks = remarks;
		this.fb_unique_lead_id = fb_unique_lead_id;
		this.link_clicked = link_clicked;
		this.smsStatus = smsStatus;
		this.missed_call = missed_call;
		this.units = units;
		this.call_center_update = call_center_update;
		this.cid = cid;
		this.utm_source = utm_source;
		this.utm_campaign = utm_campaign;
		this.utm_medium = utm_medium;
		this.utm_content = utm_content;
		this.created_date = created_date;
		this.modified_date = modified_date;
		this.days=days;
	}
	public Long getLead_id() {
		return lead_id;
	}
	public Integer getFacebook_form_id() {
		return facebook_form_id;
	}
	public String getFacebook_call_CID() {
		return facebook_call_CID;
	}
	public String getRemarks() {
		return remarks;
	}
	public String getFb_unique_lead_id() {
		return fb_unique_lead_id;
	}
	public String getLink_clicked() {
		return link_clicked;
	}
	public String getSmsStatus() {
		return smsStatus;
	}
	public String getMissed_call() {
		return missed_call;
	}
	public String getUnits() {
		return units;
	}
	public Integer getCall_center_update() {
		return call_center_update;
	}
	public String getCid() {
		return cid;
	}
	public String getUtm_source() {
		return utm_source;
	}
	public String getUtm_campaign() {
		return utm_campaign;
	}
	public String getUtm_medium() {
		return utm_medium;
	}
	public String getUtm_content() {
		return utm_content;
	}
	public String getCreated_date() {
		return created_date;
	}
	public String getModified_date() {
		return modified_date;
	}
	public void setLead_id(long lead_id) {
		this.lead_id = lead_id;
	}
	public void setFacebook_form_id(int facebook_form_id) {
		this.facebook_form_id = facebook_form_id;
	}
	public void setFacebook_call_CID(String facebook_call_CID) {
		this.facebook_call_CID = facebook_call_CID;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public void setFb_unique_lead_id(String fb_unique_lead_id) {
		this.fb_unique_lead_id = fb_unique_lead_id;
	}
	public void setLink_clicked(String link_clicked) {
		this.link_clicked = link_clicked;
	}
	public void setSmsStatus(String smsStatus) {
		this.smsStatus = smsStatus;
	}
	public void setMissed_call(String missed_call) {
		this.missed_call = missed_call;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public void setCall_center_update(int call_center_update) {
		this.call_center_update = call_center_update;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public void setUtm_source(String utm_source) {
		this.utm_source = utm_source;
	}
	public void setUtm_campaign(String utm_campaign) {
		this.utm_campaign = utm_campaign;
	}
	public void setUtm_medium(String utm_medium) {
		this.utm_medium = utm_medium;
	}
	public void setUtm_content(String utm_content) {
		this.utm_content = utm_content;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	

}
