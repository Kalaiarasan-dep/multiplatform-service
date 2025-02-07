package in.hashconnect.gmb.vo;

public class TimePeriod {

	private String openDay;
	//private String openTime;
	private TimeOfDay openTime;
	private String closeDay;
	//private String closeTime;
	private TimeOfDay closeTime;

	public String getOpenDay() {
		return openDay;
	}

	public void setOpenDay(String openDay) {
		this.openDay = openDay;
	}

		public String getCloseDay() {
		return closeDay;
	}

	public void setCloseDay(String closeDay) {
		this.closeDay = closeDay;
	}

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

	
}
