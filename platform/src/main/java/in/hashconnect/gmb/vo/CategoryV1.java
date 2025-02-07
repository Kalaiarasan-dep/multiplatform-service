package in.hashconnect.gmb.vo;

import java.util.List;

public class CategoryV1 {

  private String name;
  private String displayName;
  private List<ServiceType> serviceTypes;
  private List<MoreHoursType> moreHoursTypes;
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public List<MoreHoursType> getMoreHoursTypes() {
    return moreHoursTypes;
  }
  public void setMoreHoursTypes(List<MoreHoursType> moreHoursTypes) {
    this.moreHoursTypes = moreHoursTypes;
  }
  public List<ServiceType> getServiceTypes() {
    return serviceTypes;
  }
  public void setServiceTypes(List<ServiceType> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }
  
}

