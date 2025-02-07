package in.hashconnect.gmb.vo;

import static in.hashconnect.util.StringUtil.escapeHtml;

public class LocalPostEvent {

	private String title;
	private TimeInterval schedule;

	public LocalPostEvent() {

	}

	public LocalPostEvent(String title, TimeInterval schedule) {
		super();
		this.title = title;
		this.schedule = schedule;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? title : escapeHtml(title);
	}

	public TimeInterval getSchedule() {
		return schedule;
	}

	public void setSchedule(TimeInterval schedule) {
		this.schedule = schedule;
	}

}
