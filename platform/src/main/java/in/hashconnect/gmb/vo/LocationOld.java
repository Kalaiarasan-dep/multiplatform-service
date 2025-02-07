package in.hashconnect.gmb.vo;

import java.util.List;

public class LocationOld {

	private String name;
	private String locationName;
	private String languageCode;
	private String storeCode;
	private String primaryPhone;
	private String websiteUrl;
	private List<String> additionalPhones;
	private PostalAddress address;
	private Category primaryCategory;
	private List<Category> additionalCategories;
	private BusinessHours regularHours;
	private List<SpecialHours> specialHourPeriods;
	private ServiceAreaBusiness serviceArea;
	private LocationKey locationKey;
	private List<String> labels;
	private AdWordsLocationExtensions adWordsLocationExtensions;
	private LatLng latlng;
	private OpenInfo openInfo;
	private LocationState locationState;
	private List<Attribute> attributes;
	private MetaData metadata;
	private List<PriceList> priceLists;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getPrimaryPhone() {
		return primaryPhone;
	}

	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public List<String> getAdditionalPhones() {
		return additionalPhones;
	}

	public void setAdditionalPhones(List<String> additionalPhones) {
		this.additionalPhones = additionalPhones;
	}

	public PostalAddress getAddress() {
		return address;
	}

	public void setAddress(PostalAddress address) {
		this.address = address;
	}

	public Category getPrimaryCategory() {
		return primaryCategory;
	}

	public void setPrimaryCategory(Category primaryCategory) {
		this.primaryCategory = primaryCategory;
	}

	public List<Category> getAdditionalCategories() {
		return additionalCategories;
	}

	public void setAdditionalCategories(List<Category> additionalCategories) {
		this.additionalCategories = additionalCategories;
	}

	public BusinessHours getRegularHours() {
		return regularHours;
	}

	public void setRegularHours(BusinessHours regularHours) {
		this.regularHours = regularHours;
	}

	public List<SpecialHours> getSpecialHourPeriods() {
		return specialHourPeriods;
	}

	public void setSpecialHourPeriods(List<SpecialHours> specialHourPeriods) {
		this.specialHourPeriods = specialHourPeriods;
	}

	public ServiceAreaBusiness getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(ServiceAreaBusiness serviceArea) {
		this.serviceArea = serviceArea;
	}

	public LocationKey getLocationKey() {
		return locationKey;
	}

	public void setLocationKey(LocationKey locationKey) {
		this.locationKey = locationKey;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public AdWordsLocationExtensions getAdWordsLocationExtensions() {
		return adWordsLocationExtensions;
	}

	public void setAdWordsLocationExtensions(
			AdWordsLocationExtensions adWordsLocationExtensions) {
		this.adWordsLocationExtensions = adWordsLocationExtensions;
	}

	public LatLng getLatlng() {
		return latlng;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}

	public OpenInfo getOpenInfo() {
		return openInfo;
	}

	public void setOpenInfo(OpenInfo openInfo) {
		this.openInfo = openInfo;
	}

	public LocationState getLocationState() {
		return locationState;
	}

	public void setLocationState(LocationState locationState) {
		this.locationState = locationState;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public MetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(MetaData metadata) {
		this.metadata = metadata;
	}

	public List<PriceList> getPriceLists() {
		return priceLists;
	}

	public void setPriceLists(List<PriceList> priceLists) {
		this.priceLists = priceLists;
	}
}
