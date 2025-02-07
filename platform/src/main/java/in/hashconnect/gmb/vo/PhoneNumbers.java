package in.hashconnect.gmb.vo;

import java.util.List;

public class PhoneNumbers {
  
  private String primaryPhone;
  private List<String> additionalPhones;
  
  public String getPrimaryPhone() {
    return primaryPhone;
  }
  public void setPrimaryPhone(String primaryPhone) {
    this.primaryPhone = primaryPhone;
  }
  public List<String> getAdditionalPhones() {
    return additionalPhones;
  }
  public void setAdditionalPhones(List<String> additionalPhones) {
    this.additionalPhones = additionalPhones;
  }
  

}
