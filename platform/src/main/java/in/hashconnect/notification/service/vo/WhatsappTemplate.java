package in.hashconnect.notification.service.vo;

import java.util.ArrayList;
import java.util.List;

public class WhatsappTemplate {
	private String name;
	private WhatsappLanguage language;
	private List<WhatsappComponent> components;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WhatsappLanguage getLanguage() {
		return this.language;
	}

	public void setLanguage(WhatsappLanguage language) {
		this.language = language;
	}

	public List<WhatsappComponent> getComponents() {
		return this.components;
	}

	public void setComponents(ArrayList<WhatsappComponent> components) {
		this.components = components;
	}

}
