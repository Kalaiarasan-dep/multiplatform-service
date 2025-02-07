package in.hashconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UploadProcessorFactory {

	@Autowired
	private UploadProcessor orderUploadProcessor;

	public UploadProcessor get(String type) {
		switch (type) {
		case "bulk":
			return orderUploadProcessor;
		}
		throw new UnsupportedOperationException(type + " not supported");
	}
}
