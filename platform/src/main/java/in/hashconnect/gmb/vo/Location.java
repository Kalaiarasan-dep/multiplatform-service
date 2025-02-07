package in.hashconnect.gmb.vo;

import java.util.List;

public class Location {

    private String name;
    private String languageCode;
    private String storeCode;
    private String title;
    private PhoneNumbers phoneNumbers;
    private CategoriesV1 categories;
    private PostalAddress storefrontAddress;
    private String websiteUri;
    private BusinessHours regularHours;
    private SpecialHours specialHours;
    private ServiceAreaBusiness serviceArea;
    private List<String> labels;
    private AdWordsLocationExtensions adWordsLocationExtensions;
    private LatLng latlng;
    private OpenInfo openInfo;
    private MetaData metadata;
    private Profile profile;
    private RelationshipData relationshipData;
    private List<MoreHours> moreHours;
    private List<ServiceItem> serviceItems;
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BusinessHours getRegularHours() {
        return regularHours;
    }

    public void setRegularHours(BusinessHours regularHours) {
        this.regularHours = regularHours;
    }

    public ServiceAreaBusiness getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(ServiceAreaBusiness serviceArea) {
        this.serviceArea = serviceArea;
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

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public PhoneNumbers getPhoneNumbers() {
      return phoneNumbers;
    }

    public void setPhoneNumbers(PhoneNumbers phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
    }

    public CategoriesV1 getCategories() {
      return categories;
    }

    public void setCategories(CategoriesV1 categories) {
      this.categories = categories;
    }

    public PostalAddress getStorefrontAddress() {
      return storefrontAddress;
    }

    public void setStorefrontAddress(PostalAddress storefrontAddress) {
      this.storefrontAddress = storefrontAddress;
    }

    public String getWebsiteUri() {
      return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
      this.websiteUri = websiteUri;
    }

    public SpecialHours getSpecialHours() {
      return specialHours;
    }

    public void setSpecialHours(SpecialHours specialHours) {
      this.specialHours = specialHours;
    }

    public Profile getProfile() {
      return profile;
    }

    public void setProfile(Profile profile) {
      this.profile = profile;
    }

    public RelationshipData getRelationshipData() {
      return relationshipData;
    }

    public void setRelationshipData(RelationshipData relationshipData) {
      this.relationshipData = relationshipData;
    }

    public List<MoreHours> getMoreHours() {
      return moreHours;
    }

    public void setMoreHours(List<MoreHours> moreHours) {
      this.moreHours = moreHours;
    }

    public List<ServiceItem> getServiceItems() {
      return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
      this.serviceItems = serviceItems;
    }

   
  
}
