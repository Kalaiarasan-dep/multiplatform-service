package in.hashconnect.controller;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.service.PartnerService;

/*
 * This is to save the partner invoice validation results
 * and to provide the invoice details by invid
 */

@RestController
@RequestMapping("/inv-req")
public class InvoiceValidationController {
	
	
	@Autowired
	private PartnerService partnerService;
	@Autowired
	private PartnerDao partnerDao;
		
	@GetMapping("/details")
	public Response<Map<String, Object>> getPartnerInvDetails(@RequestParam("invid") String invId) {
		Map<String, Object> map = partnerService.getPartnerInvoiceDtls(invId);
		Response<Map<String, Object>> resp = Response.ok();
		
		if (!MapUtils.getBooleanValue(map, "valid_status")) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("Api is not returning data. Because action can't be performed against the inv status");
			return resp;
		}
		
		resp.setData(map);
		return resp;
	}
	
	
	@PostMapping("/validation-results")
	public Response<?> saveInvValidation(@RequestBody Map<String, Object> validationResp) {
		if (validationResp == null) {
			return Response.failed("Invalid input provided");
		}
		partnerService.saveInvValidation(validationResp);
		return Response.ok();
	}
	
	@GetMapping("/validate-invid")
	public Response<?> validateInvId(@RequestParam("invid") String invId,
			@RequestParam("partner_id") Integer partnerId,@RequestParam("invReqId") Long invReqId) {
		Response<String> resp = Response.ok();
		if (partnerDao.isInvoiceExistInFinancialYrForPartner(invId, partnerId,invReqId)) {
			resp.setStatus(STATUS.FAILED);
			resp.setData("Invoice already exists");
		}
		return resp;
	}

}
