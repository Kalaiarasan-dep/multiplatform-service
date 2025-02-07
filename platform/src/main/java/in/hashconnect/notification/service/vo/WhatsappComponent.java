package in.hashconnect.notification.service.vo;

import java.util.ArrayList;
import java.util.List;

public class WhatsappComponent {
	private String type;
	private List<Parameters> parameters;


	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Parameters> getParameters() {
		return this.parameters;
	}

	public void setParameters(ArrayList<Parameters> parameters) {
		this.parameters = parameters;
	}

}