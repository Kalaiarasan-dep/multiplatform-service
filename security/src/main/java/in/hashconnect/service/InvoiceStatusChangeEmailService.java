package in.hashconnect.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.MapUtils.getString;
import static org.apache.commons.collections.MapUtils.getInteger;
import org.springframework.stereotype.Service;

import in.hashconnect.util.InvoiceReasonsDto;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.vo.RequestTemplateTypes;

/**
 * To send email for invoice status change. 
 * Status Invoice requested, submitted, approved, onhold and rejected
 */
@Service
public class InvoiceStatusChangeEmailService extends AbstractEmailService 
									implements ReqStatusChangeEmailService {
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyStatusChange(List<Object> invIds, RequestTemplateTypes type) {
		if (invIds == null || invIds.isEmpty()) {
			return;
		}
		String reqTypeStr = settingsUtil.getValue("valid_req_types");
		Map<String, List<Integer>>  reqTypeMap  = JsonUtil.readValue(reqTypeStr, Map.class);
		List<Map<String, Object>> emailDtlsList = partnerDao.getPartnerEmailByInvIds(invIds);
		List<Integer> invoiceStatuses = reqTypeMap.get(RequestTemplateTypes.INVOICE.name());
		Map<String, Map<String, Object>> inputDtlsMap = new HashMap<String, Map<String, Object>>();
		emailDtlsList.stream().forEach( map -> {
			if(isValidReqStatus(getInteger(map, "statusId"), invoiceStatuses)) {
				map.put("template", getTemplateTye((Integer)map.get("statusId")));
				//if invoice status is not invoice requested status then get the reasons
				if (!isInvRequestedStatus(getInteger(map, "statusId"))) {
					InvoiceReasonsDto dto =  getInvReasonsByIdAndStatus(getString(map, "id"), getString(map, "statusId"));
					if ( dto != null) {
						map.put("addtl_info", dto.addtlInfo());
						map.put("action_required", dto.actionReqired());
					}
					map.put("subject", StringUtil.concate(map.get("status_name")," - Lenovo ", map.get("program_name"),
							" Request ID: ", map.get("id")));
				} else {
					map.put("subject", StringUtil.concate("Invoice Request Notification - Lenovo ", map.get("program_name"),
							" Request ID: ", map.get("id")));
				}
				inputDtlsMap.put(String.valueOf(map.get("id")), map);
				
			}
		});
		sendEmail(inputDtlsMap);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private InvoiceReasonsDto getInvoiceReasons(String invId, String statusId) {
		String validationJsonResp = partnerDao.getInvoiceReasons(invId, statusId);
		if (!StringUtil.isValid(validationJsonResp)) {
			return null;
		}
		Map<String, Object> map =  JsonUtil.readValue(validationJsonResp, Map.class);
		
		List<Map<String, Object>> list = (List)map.get("reasons");
		ArrayList<Object> reasonIds =  list.stream().collect(ArrayList::new, 
				(resp, reasonMap) -> resp.add(reasonMap.get("id")),
				ArrayList::add);
		
		InvoiceReasonsDto dto =  partnerDao.getInvReasonAddtlInfo(reasonIds, statusId);
		return dto;
		
	}
	
	private boolean isInvRequestedStatus(Integer statusId) {
		Integer inv_requested_status = settingsUtil.getIntValue("inv_requested_status");
		List<Integer> INV_REQUESTED_ID = Arrays.asList(inv_requested_status!= null ?
				inv_requested_status:0);
		
		if (INV_REQUESTED_ID.contains(statusId)) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private InvoiceReasonsDto getInvReasonsByIdAndStatus(String id, String statusId) {
		String inv_reason_by_status_ids = settingsUtil.getValue("inv_reason_status_ids");
		List<String> list = JsonUtil.readValue(inv_reason_by_status_ids, List.class);
		if (list != null && list.contains(statusId)) {
			return partnerDao.getInvReasonByStatusId(statusId);
		}

		return getInvoiceReasons(id, statusId);
	}
}
