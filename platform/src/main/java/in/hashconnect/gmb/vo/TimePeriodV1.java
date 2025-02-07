package in.hashconnect.gmb.vo;

public class TimePeriodV1 {

  private DayOfWeek openDay;
  private TimeOfDay openTime;
  private DayOfWeek closeDay;
  private TimeOfDay closeTime;
  
  public TimeOfDay getOpenTime() {
    return openTime;
  }
  public void setOpenTime(TimeOfDay openTime) {
    this.openTime = openTime;
  }
  
  public TimeOfDay getCloseTime() {
    return closeTime;
  }
  public void setCloseTime(TimeOfDay closeTime) {
    this.closeTime = closeTime;
  }
  public DayOfWeek getOpenDay() {
    return openDay;
  }
  public void setOpenDay(DayOfWeek openDay) {
    this.openDay = openDay;
  }
  public DayOfWeek getCloseDay() {
    return closeDay;
  }
  public void setCloseDay(DayOfWeek closeDay) {
    this.closeDay = closeDay;
  }
  
  
}
