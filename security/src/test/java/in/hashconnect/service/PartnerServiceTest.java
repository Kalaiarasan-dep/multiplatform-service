package in.hashconnect.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.admin.dao.impl.PartnerDaoImpl;

public class PartnerServiceTest {
	
	private PartnerService partnerService = 
			mock(PartnerServiceImpl.class);
	private PartnerDao partnerDao = mock(PartnerDaoImpl.class);

	//@Test
	void testSaveInvoiceValidationResp() {
		Map<String, Object> resp = new HashMap<>();
		resp.put("data", "{\"docNo\":\"160\",\"source\":\"OMO\",\"inv_number\":\"inv12345\",\"details\":[{\"fieldId\":3,\"actionId\":3,\"optionId\":7,\"fieldName\":\"Invoice11\",\"actionName\":\"Approve\",\"criteriaId\":3,\"optionName\":\"Unclear\",\"criteriaName\":\"Does the Shared invoice is TAX Invoice/Original Invoice?\"},{\"fieldId\":4,\"actionId\":3,\"optionId\":11,\"fieldName\":\"Invoice2\",\"actionName\":\"Approve\",\"criteriaId\":4,\"optionName\":\"Unclear\",\"criteriaName\":\"Does the Name on the Invoice match with the above mentioned Partner name?\"},{\"fieldId\":5,\"actionId\":3,\"optionId\":15,\"fieldName\":\"Invoice3\",\"actionName\":\"Approve\",\"criteriaId\":5,\"optionName\":\"Missing\",\"criteriaName\":\"Does the GST Number on the Invoice match with the above mentioned Partner GST Number?\"},{\"fieldId\":6,\"actionId\":3,\"optionId\":20,\"fieldName\":\"Invoice4\",\"actionName\":\"Approve\",\"criteriaId\":6,\"optionName\":\"Unclear\",\"criteriaName\":\"Is the Billing Name on the invoice is \\\"Hash Connect Integrated Services Pvt Ltd\\\"?\"},{\"fieldId\":7,\"actionId\":3,\"optionId\":25,\"fieldName\":\"Invoice5\",\"actionName\":\"Approve\",\"criteriaId\":7,\"optionName\":\"Invalid\",\"criteriaName\":\"Is the Billing GST Number on the Invoice is \\\"29AADCH4124N1ZC\\\"?\"},{\"fieldId\":8,\"actionId\":3,\"optionId\":28,\"fieldName\":\"Invoice6\",\"actionName\":\"Approve\",\"criteriaId\":8,\"optionName\":\"Unclear\",\"criteriaName\":\"Is the Billing Address on the Invoice is \\\"248, Samhita Plaza, 4th Floor, 80 Feet Rd, Defence Colony, Indiranagar, Bengaluru, Karnataka 560038\\\"?\"},{\"fieldId\":9,\"actionId\":3,\"optionId\":33,\"fieldName\":\"Invoice7\",\"actionName\":\"Approve\",\"criteriaId\":9,\"optionName\":\"Invalid\",\"criteriaName\":\"Is the HSN Code mentioned on the Invoice as 998599?\"},{\"fieldId\":10,\"actionId\":3,\"optionId\":36,\"fieldName\":\"Invoice8\",\"actionName\":\"Approve\",\"criteriaId\":10,\"optionName\":\"Unclear\",\"criteriaName\":\"Does the Discription provided on the Invoice as \\\"Other Support Services\\\"?\"},{\"fieldId\":11,\"actionId\":1,\"optionId\":40,\"fieldName\":\"Invoice9\",\"actionName\":\"Approve\",\"criteriaId\":11,\"optionName\":\"Yes with the difference of + or - Rs.2\",\"criteriaName\":\"Does the Cost on the invoice match with the requested cost mentioned above?\"},{\"fieldId\":12,\"actionId\":3,\"optionId\":46,\"fieldName\":\"Invoice10\",\"actionName\":\"Approve\",\"criteriaId\":12,\"optionName\":\"Yes but CGST and SGST Break up provided\",\"criteriaName\":\"Does the GST Break up on the Invoice is matching with the above mentioned GST Break Up?\"},{\"fieldId\":13,\"actionId\":1,\"optionId\":51,\"fieldName\":\"Invoice11\",\"actionName\":\"Approve\",\"criteriaId\":13,\"optionName\":\"Yes with the difference of + or - Rs.2\",\"criteriaName\":\"Does the total Invoice Amount on the invoice match with the above mentioned requested amount ?\"},{\"fieldId\":14,\"actionId\":3,\"optionId\":60,\"fieldName\":\"Invoice12\",\"actionName\":\"Approve\",\"criteriaId\":14,\"optionName\":\"Invalid\",\"criteriaName\":\"Does the Seal and Signature is available on the invoice?\"},{\"fieldId\":15,\"actionId\":3,\"optionId\":63,\"fieldName\":\"Invoice13\",\"actionName\":\"Approve\",\"criteriaId\":15,\"optionName\":\"Yes but entered Invoice Date is Wrong\",\"criteriaName\":\"Does the Invoice Raised on or after the requested date mentioned above?\"},{\"fieldId\":16,\"actionId\":3,\"optionId\":68,\"fieldName\":\"Invoice14\",\"actionName\":\"Approve\",\"criteriaId\":16,\"optionName\":\"Duplicate Invoice\",\"criteriaName\":\"Does the Invoice Number Entered is matching with the Invoice?\"},{\"fieldId\":17,\"actionId\":1,\"optionId\":72,\"fieldName\":\"Invoice15\",\"actionName\":\"Approve\",\"criteriaId\":17,\"optionName\":\"Not mentioned on the Invoice but the Invoice Amount matching with the Requested Amount\",\"criteriaName\":\"Does the Orders mentioned on the invoice is matching with the requested order IDs?\"}],\"docType\":\"Invoice\",\"reasons\":[{\"id\":1,\"name\":\"scan unclear\"}],\"remarks\":\"\",\"selectedActionId\":1,\"suggestedActionId\":1,\"selectedActionName\":\"Approve\",\"suggestedActionName\":\"Approve\"}");
			
		when(partnerDao.getStatusIdByAction(anyString(), anyInt())).thenCallRealMethod();
		when(partnerService.saveInvValidation(resp)).thenCallRealMethod();
		boolean success = partnerService.saveInvValidation(resp);
		assertFalse(success);
	}

}
