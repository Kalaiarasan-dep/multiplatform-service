package in.hashconnect.service;

import java.util.List;
import java.util.Map;

import in.hashconnect.util.BillsVo;
import in.hashconnect.util.SOASummary;
import in.hashconnect.vo.PartnerResponse;

public interface PartnerService {
	
	PartnerResponse validatePartner(Map<String, Object> invoiceDtls);
	void submitInvoice(Map<String, Object> invoiceDtls);
	Map<String, Object> getPartnerInvoiceDtls(String invId);
	boolean saveInvValidation(Map<String, Object> resp);	
	List<BillsVo> getBillReport(String partnerId, SOASummary summary);
	Map<String, Object> getPaymentDtlsByInvId(Integer invId);
	Map<String, Object> getPartnerGSTAndBankDetailsByInvNo(String invId);
	Map<String, String> getPartnerBannerDtls(String userId);
	String getUserRoleByUserId(Integer userId);
	void savePartnerStatus(Long userId,Integer statusId,Long partnerId,String type);
	Map<String, Object> getPartnerValidationDtls(String userId);
	boolean saveSignUpValidation(Map<String, Object> resp);
	List<String> getPartnerList();

}
