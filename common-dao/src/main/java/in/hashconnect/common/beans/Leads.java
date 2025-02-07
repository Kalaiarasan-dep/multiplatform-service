package in.hashconnect.common.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Leads {
	private Long id;
	private Integer source_id;
	private String first_name;
	private String last_name;
	private String mobile_no;
	private String mobile_no2;
	private String email_id;
	private String landline_no1;
	private String address;
	private String pincode;
	private String facebook_id;
	private String twitter_id;
	private Integer circle_id;
	private Integer city_id;
	private String created_date;
	private String modified_date;
	private String company_name;
	private Integer active_status;
	private String encoded;
	private Integer statusId;
	// Amar
	private Integer prodId;
	private Map<String, String> parsedValues;
	private LeadsExtra leadsExtra;
	private List<Long> dealerList;
	private List<LeadsStoreMapping> leadsStoreMapping;
	
	private Integer storeId;
	private String notificationCount;
	private String dateTime;
	private String bdInterest;
	private Long bdRefId;
	private Integer leadStoreMapId;
	private String comments;
	private Integer verificationId;

	public Leads() {
		super();
	}

	public Leads(String first_name, String last_name, String mobile_no, String mobile_no2, String email_id,
			String landline_no1, String address, String pincode, String facebook_id, String twitter_id,
			Integer circle_id, Integer city_id, String company_name, String created_date, String modified_date,
			Integer active_status) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.mobile_no = mobile_no;
		this.mobile_no2 = mobile_no2;
		this.email_id = email_id;
		this.landline_no1 = landline_no1;
		this.address = address;
		this.pincode = pincode;
		this.facebook_id = facebook_id;
		this.twitter_id = twitter_id;
		this.circle_id = circle_id;
		this.city_id = city_id;
		this.company_name = company_name;
		this.created_date = created_date;
		this.modified_date = modified_date;
		this.active_status = active_status;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getMobile_no() {
		return mobile_no;
	}

	public String getMobile_no2() {
		return mobile_no2;
	}

	public String getEmail_id() {
		return email_id;
	}

	public String getLandline_no1() {
		return landline_no1;
	}

	public String getAddress() {
		return address;
	}

	public String getPincode() {
		return pincode;
	}

	public String getFacebook_id() {
		return facebook_id;
	}

	public String getTwitter_id() {
		return twitter_id;
	}

	public Integer getCircle_id() {
		return circle_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public String getCreated_date() {
		return created_date;
	}

	public String getModified_date() {
		return modified_date;
	}

	public Leads setFirst_name(String first_name) {
		this.first_name = first_name;
		return this;
	}

	public Leads setLast_name(String last_name) {
		this.last_name = last_name;
		return this;
	}

	public Leads setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
		return this;
	}

	public Leads setMobile_no2(String mobile_no2) {
		this.mobile_no2 = mobile_no2;
		return this;
	}

	public Leads setEmail_id(String email_id) {
		this.email_id = email_id;
		return this;
	}

	public Leads setLandline_no1(String landline_no1) {
		this.landline_no1 = landline_no1;
		return this;
	}

	public Leads setAddress(String address) {
		this.address = address;
		return this;
	}

	public Leads setPincode(String pincode) {
		this.pincode = pincode;
		return this;
	}

	public Leads setFacebook_id(String facebook_id) {
		this.facebook_id = facebook_id;
		return this;
	}

	public Leads setTwitter_id(String twitter_id) {
		this.twitter_id = twitter_id;
		return this;
	}

	public Leads setCircle_id(Integer circle_id) {
		this.circle_id = circle_id;
		return this;
	}

	public Leads setCity_id(Integer city_id) {
		this.city_id = city_id;
		return this;
	}

	public Leads setCreated_date(String created_date) {
		this.created_date = created_date;
		return this;
	}

	public Leads setModified_date(String modified_date) {
		this.modified_date = modified_date;
		return this;
	}

	public String getCompany_name() {
		return company_name;
	}

	public Leads setCompany_name(String company_name) {
		this.company_name = company_name;
		return this;
	}

	public Integer getActive_status() {
		return active_status;
	}

	public Leads setActive_status(Integer active_status) {
		this.active_status = active_status;
		return this;
	}

	public String getEncoded() {
		return encoded;
	}

	public Leads setEncoded(String encoded) {
		this.encoded = encoded;
		return this;
	}

	public Integer getSource_id() {
		return source_id;
	}

	public int setSource_id(Integer source_id) {
		return this.source_id = source_id;
	}

	public Integer getProdId() {
		return prodId;
	}

	public Leads setProdId(Integer prodId) {
		this.prodId = prodId;
		return this;
	}

	public Map<String, String> getParsedValues() {
		return parsedValues;
	}

	public Leads setParsedValues(Map<String, String> parsedValues) {
		this.parsedValues = parsedValues;
		return this;
	}

	public LeadsExtra getLeadsExtra() {
		return leadsExtra;
	}

	public Leads setLeadsExtra(LeadsExtra leadsExtra) {
		this.leadsExtra = leadsExtra;
		return this;
	}

	public List<Long> getDealerList() {
		return dealerList;
	}

	public Leads setDealerList(List<Long> dealerList) {
		this.dealerList = dealerList;
		return this;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public Leads setStatusId(Integer statusId) {
		this.statusId = statusId;
		return this;
	}

	@Override
	public String toString() {
		return "Leads [source_id=" + source_id + ", first_name=" + first_name + ", last_name=" + last_name
				+ ", mobile_no=" + mobile_no + ", mobile_no2=" + mobile_no2 + ", email_id=" + email_id
				+ ", landline_no1=" + landline_no1 + ", address=" + address + ", pincode=" + pincode + ", facebook_id="
				+ facebook_id + ", twitter_id=" + twitter_id + ", circle_id=" + circle_id + ", city_id=" + city_id
				+ ", created_date=" + created_date + ", modified_date=" + modified_date + ", company_name="
				+ company_name + ", active_status=" + active_status + ", encoded=" + encoded + ", statusId=" + statusId
				+ ", prodId=" + prodId + ", parsedValues=" + parsedValues + ", leadsExtra=" + leadsExtra
				+ ", dealerList=" + dealerList + ", comments=" + comments+ ",  verificationId=" + verificationId + ", getFirst_name()=" + getFirst_name() + ", getLast_name()="
				+ getLast_name() + ", getMobile_no()=" + getMobile_no() + ", getMobile_no2()=" + getMobile_no2()
				+ ", getEmail_id()=" + getEmail_id() + ", getLandline_no1()=" + getLandline_no1() + ", getAddress()="
				+ getAddress() + ", getPincode()=" + getPincode() + ", getFacebook_id()=" + getFacebook_id()
				+ ", getTwitter_id()=" + getTwitter_id() + ", getCircle_id()=" + getCircle_id() + ", getCity_id()="
				+ getCity_id() + ", getCreated_date()=" + getCreated_date() + ", getModified_date()="
				+ getModified_date() + ", getCompany_name()=" + getCompany_name() + ", getActive_status()="
				+ getActive_status() + ", getEncoded()=" + getEncoded() + ", getSource_id()=" + getSource_id()
				+ ", getProdId()=" + getProdId() + ", getParsedValues()=" + getParsedValues() + ", getDealerList()="
				+ getDealerList() + ", getStatusId()=" + getStatusId() + ", getComments()=" + getComments() + ", getVerificationId()= " + getVerificationId() + " ]";
	}

	public List<LeadsStoreMapping> getLeadsStoreMapping() {
		if(leadsStoreMapping == null)
			leadsStoreMapping = new ArrayList<>();
		return leadsStoreMapping;
	}

	public Leads setLeadsStoreMapping(List<LeadsStoreMapping> leadsStoreMapping) {
		this.leadsStoreMapping = leadsStoreMapping;
		return this;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(String notificationCount) {
		this.notificationCount = notificationCount;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getBdInterest() {
		return bdInterest;
	}

	public void setBdInterest(String bdInterest) {
		this.bdInterest = bdInterest;
	}

	public Long getBdRefId() {
		return bdRefId;
	}

	public void setBdRefId(Long bdRefId) {
		this.bdRefId = bdRefId;
	}

	public Integer getLeadStoreMapId() {
		return leadStoreMapId;
	}

	public void setLeadStoreMapId(Integer leadStoreMapId) {
		this.leadStoreMapId = leadStoreMapId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getVerificationId() {
		return verificationId;
	}

	public void setVerificationId(Integer verificationId) {
		this.verificationId = verificationId;
	}

}
