package in.hashconnect.gmb.vo;

public class SpecialHourPeriod {
	private Date startDate;
	private TimeOfDay openTime;
	private Date endDate;
	private TimeOfDay closeTime; 
	private boolean closed;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

		public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
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
