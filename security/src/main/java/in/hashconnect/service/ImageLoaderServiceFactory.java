package in.hashconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageLoaderServiceFactory {

	@Autowired
	private InvoiceImageLoaderService invoiceImageLoaderService;

	@Autowired
	private SignUpImageLoaderService signUpImageLoaderService;

	public ImageLoaderService get(String type) {
		switch (type) {
		case "ir": {
				return invoiceImageLoaderService;
		}
		case "signup": {
			return signUpImageLoaderService;
		}
		default:
			throw new IllegalArgumentException("Configured types are: pr/signup");
		}
	}
}
