package in.hashconnect.gmb.vo;

public class TimeInterval {

	private Date startDate;
	private TimeOfDay startTime;
	private Date endDate;
	private TimeOfDay endTime;
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public TimeOfDay getStartTime() {
		return startTime;
	}
	public void setStartTime(TimeOfDay startTime) {
		this.startTime = startTime;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public TimeOfDay getEndTime() {
		return endTime;
	}
	public void setEndTime(TimeOfDay endTime) {
		this.endTime = endTime;
	}
}
