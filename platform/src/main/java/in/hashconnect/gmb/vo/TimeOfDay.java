package in.hashconnect.gmb.vo;

public class TimeOfDay {
	private Integer hours;
	private Integer minutes;
	private Integer seconds;
	private Integer nanos;
	
	public TimeOfDay() {
		// TODO Auto-generated constructor stub
	}
	
	public TimeOfDay(Integer hours, Integer minutes, Integer seconds, Integer nanos) {
		super();
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.nanos = nanos;
	}

	public Integer getHours() {
		return hours;
	}
	public void setHours(Integer hours) {
		this.hours = hours;
	}
	public Integer getMinutes() {
		return minutes;
	}
	public void setMinutes(Integer minutes) {
		this.minutes = minutes;
	}
	public Integer getSeconds() {
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}
	public Integer getNanos() {
		return nanos;
	}
	public void setNanos(Integer nanos) {
		this.nanos = nanos;
	}
	

}
