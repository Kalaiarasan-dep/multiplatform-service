package in.hashconnect.notification.service.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WhatsAppNotification extends Notification {
	public enum VENDOR_TYPE {
		EISENSY
	};

	private String messaging_product;
	private String recipient_type;
	private String waType;
	private WhatsappTemplate waTemplate;

	@JsonIgnore
	private Map<String, Object> response;

	public String getMessaging_product() {
		return this.messaging_product;
	}

	public void setMessaging_product(String messaging_product) {
		this.messaging_product = messaging_product;
	}

	public String getRecipient_type() {
		return this.recipient_type;
	}

	public void setRecipient_type(String recipient_type) {
		this.recipient_type = recipient_type;
	}

	@JsonProperty("type")
	public String getWaType() {
		return this.waType;
	}

	public void setWaType(String type) {
		this.waType = type;
	}

	@JsonProperty("template")
	public WhatsappTemplate getWaTemplate() {
		return this.waTemplate;
	}

	public void setWaTemplate(WhatsappTemplate template) {
		this.waTemplate = template;
	}

	public Map<String, Object> getResponse() {
		return response;
	}

	public void setResponse(Map<String, Object> response) {
		this.response = response;
	}
}
