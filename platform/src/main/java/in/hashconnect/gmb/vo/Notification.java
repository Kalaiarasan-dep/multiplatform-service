package in.hashconnect.gmb.vo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Notification {
	private String to;
	private Map<String, String> data;
	@JsonIgnore
	private List<Long> storeIds;
	private Map<String, String> notification;
	private Map<String, String> android;
	private List<String> registration_ids;

	public List<String> getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Map<String, String> getNotification() {
		return notification;
	}

	public void setNotification(Map<String, String> notification) {
		this.notification = notification;
	}

	public List<Long> getStoreIds() {
		return storeIds;
	}

	public void setStoreIds(List<Long> storeIds) {
		this.storeIds = storeIds;
	}

	@Override
	public String toString() {
		return "Notification [to=" + to + ", data=" + data + ", storeIds="
				+ storeIds + ", notification=" + notification + ", android="
				+ android + ", registration_ids=" + registration_ids + "]";
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public Map<String, String> getAndroid() {
		return android;
	}

	public void setAndroid(Map<String, String> android) {
		this.android = android;
	}
}
