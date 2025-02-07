package in.hashconnect.gmb.vo;

import java.util.List;

public class MoreHours {

  private String hoursTypeId;
  private List<TimePeriodV1> periods;
  
  public String getHoursTypeId() {
    return hoursTypeId;
  }
  public void setHoursTypeId(String hoursTypeId) {
    this.hoursTypeId = hoursTypeId;
  }
  public List<TimePeriodV1> getPeriods() {
    return periods;
  }
  public void setPeriods(List<TimePeriodV1> periods) {
    this.periods = periods;
  }
 
  
  
}
