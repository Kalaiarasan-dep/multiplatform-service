package in.hashconnect.notification.service.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "DNResponses")
public class DNResponses {

	@JacksonXmlProperty(localName = "DNResponse")
	private DNResponse dnResponse;

	public DNResponse getDnResponse() {
		return dnResponse;
	}

	public void setDnResponse(DNResponse dnResponse) {
		this.dnResponse = dnResponse;
	}
}
