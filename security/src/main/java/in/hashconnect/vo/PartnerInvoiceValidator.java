package in.hashconnect.vo;

import java.util.Map;

public interface PartnerInvoiceValidator {
	PartnerResponse validate(Map<String, Object> dtls);
	
}
