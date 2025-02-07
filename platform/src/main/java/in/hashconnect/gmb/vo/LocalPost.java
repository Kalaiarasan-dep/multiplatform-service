package in.hashconnect.gmb.vo;

import static in.hashconnect.util.StringUtil.escapeHtml;

import java.util.ArrayList;
import java.util.List;

public class LocalPost {

	private String name;
	private String languageCode;
	private String summary;
	private CallToAction callToAction;
	private String createTime;
	private String updateTime;
	private LocalPostEvent event;
	private LocalPostState state;
	private List<MediaItem> media;
	private String searchUrl;
	private LocalPostTopicType topicType;
	private AlertType alertType;
	private LocalPostOffer offer;
	private LocalPostProduct product;
	private GoogleError error;
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary == null ? summary : escapeHtml(summary);
	}

	public CallToAction getCallToAction() {
		return callToAction;
	}

	public void setCallToAction(CallToAction callToAction) {
		this.callToAction = callToAction;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public LocalPostEvent getEvent() {
		return event;
	}

	public void setEvent(LocalPostEvent event) {
		this.event = event;
	}

	public LocalPostState getState() {
		return state;
	}

	public void setState(LocalPostState state) {
		this.state = state;
	}

	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}

	public LocalPostTopicType getTopicType() {
		return topicType;
	}

	public void setTopicType(LocalPostTopicType topicType) {
		this.topicType = topicType;
	}

	public LocalPostOffer getOffer() {
		return offer;
	}

	public void setOffer(LocalPostOffer offer) {
		this.offer = offer;
	}

	public LocalPostProduct getProduct() {
		return product;
	}

	public void setProduct(LocalPostProduct product) {
		this.product = product;
	}

	public List<MediaItem> getMedia() {
		if (media == null)
			media = new ArrayList();
		return media;
	}

	public void setMedia(List<MediaItem> media) {
		this.media = media;
	}

	public GoogleError getError() {
		return error;
	}

	public void setError(GoogleError error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

}
