package in.hashconnect.notification.service.vo;

import java.util.Map;

public class Notification {
	public enum TYPE {
		SMS, EMAIL, WHATSAPP
	};

	private String template;
	private TYPE type;
	private String to;
	private String cc;
	private Map<String, Object> params;
	private Attachment attachment;
	private Long clientId;
	private String subject;
	private String from;

	public Notification() {

	}

	public Notification(String template, String to, Map<String, Object> params) {
		this.template = template;
		this.to = to;
		this.params = params;
	}
	
	public Notification(String template, String to, String subject, Map<String, Object> params) {
		this.template = template;
		this.to = to;
		this.params = params;
		this.subject = subject;
	}

	public Notification(String template, TYPE type, String to, Map<String, Object> params) {
		this.template = template;
		this.type = type;
		this.to = to;
		this.params = params;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
