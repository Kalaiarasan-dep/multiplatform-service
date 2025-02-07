package in.hashconnect.common.beans;

import in.hashconnect.common.util.StringUtil;
import static in.hashconnect.common.util.StringUtil.emptyIfNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LeadVo {

	private Long id;
	private int slNo;
	private List<Long> storeIds;
	private String name;
	private String emailId;
	private String mobileNo;
	private Integer sourceId;
	private String source;
	private String city;
	private String storeName;
	private int status;
	private String statusId;
	private String statusName;
	private Date createdDate;
	private Date modifiedDate;
	private String pincode;
	private String model;
	private String productName;
	private Long clientId;
	private Integer cityId;
	private String lenovoVerified;
	private String leadCreatedDate;
	private String modified_date;
	private Integer productId;
	private String verified;
	
	private String date;
	private Long leadStoreMapId;
	private String description;

	private String successStatus;
	private String currentStatus;
	private String serailNo;
	private String purchaseDate;
	private String registrationdate;

	private String days;
	private String userName;

	private String encoded;
	
	private String mobile_no2;

	private String landline_no1;

	private String address;
	private String interested_product;
	private String comments;
	private String call_later_date_time;
	private String demo_date_time;



	public String getEncoded() {
		return encoded;
	}


	public String getMobile_no2() {
		return mobile_no2;
	}


	public void setMobile_no2(String mobile_no2) {
		this.mobile_no2 = mobile_no2;
	}


	public String getLandline_no1() {
		return landline_no1;
	}


	public void setLandline_no1(String landline_no1) {
		this.landline_no1 = landline_no1;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setEncoded(String encoded) {
		this.encoded = encoded;
	}
	public LeadVo(List<String> values) {
		int i = 0;
		this.setDate(values.get(i++));
		this.name = values.get(i++);
		this.emailId = values.get(i++);
		this.mobileNo = values.get(i++);
		this.cityId = StringUtil.convertToInt(values.get(i++));
		this.pincode = values.get(i++);
		;
		this.source = values.get(i++);
		;
		this.clientId = StringUtil.convertToLong(values.get(i++));
	}

	public LeadVo(List<Long> storeId, String name, String emailId,
			String mobileNo, Integer sourceId, int status, String pincode) {
		this.storeIds = storeId;
		this.name = name;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.sourceId = sourceId;
		this.status = status;
		this.pincode = pincode;
	}

	public LeadVo() {
	}

	public List<Long> getStoreIds() {
		return storeIds;
	}

	public void setStoreIds(List<Long> storeIds) {
		this.storeIds = storeIds;
	}

	public String getName() {
		return emptyIfNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getSlNo() {
		return slNo;
	}

	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getProductName() {
		return emptyIfNull(productName);
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getFormattedCreatedDate() {
		if (createdDate != null)
			return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
					.format(createdDate);

		return "";
	}

	public String getFormattedModifiedDate() {
		if (modifiedDate != null)
			return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
					.format(modifiedDate);

		return "";
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getLenovoVerified() {
		return emptyIfNull(lenovoVerified);
	}

	public void setLenovoVerified(String lenovoVerified) {
		this.lenovoVerified = lenovoVerified;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getLeadStoreMapId() {
		return leadStoreMapId;
	}

	public void setLeadStoreMapId(Long leadStoreMapId) {
		this.leadStoreMapId = leadStoreMapId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getSerailNo() {
		return serailNo;
	}

	public void setSerailNo(String serailNo) {
		this.serailNo = serailNo;
	}

	public String getRegistrationdate() {
		return registrationdate;
	}

	public void setRegistrationdate(String registrationdate) {
		this.registrationdate = registrationdate;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getLeadCreatedDate() {
		return leadCreatedDate;
	}

	public void setLeadCreatedDate(String leadCreatedDate) {
		this.leadCreatedDate = leadCreatedDate;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getInterested_product() {
		return interested_product;
	}


	public void setInterested_product(String interested_product) {
		this.interested_product = interested_product;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getCall_later_date_time() {
		return emptyIfNull(call_later_date_time);
	}


	public void setCall_later_date_time(String call_later_date_time) {
		this.call_later_date_time = call_later_date_time;
	}


	public String getDemo_date_time() {
		return emptyIfNull(demo_date_time);
	
	}


	public void setDemo_date_time(String demo_date_time) {
		this.demo_date_time = demo_date_time;
	}


	public String getModified_date() {
		return emptyIfNull(modified_date);
	}


	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}


	public String getVerified() {
		return verified;
	}


	public void setVerified(String verified) {
		this.verified = verified;
	}
}
