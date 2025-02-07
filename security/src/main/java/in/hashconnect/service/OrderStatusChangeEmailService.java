package in.hashconnect.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.hashconnect.util.JsonUtil;
import in.hashconnect.vo.RequestTemplateTypes;

/**
 * To send email for order status change. 
 * Status onhold and rejected
 */
public class OrderStatusChangeEmailService extends AbstractEmailService 
									implements ReqStatusChangeEmailService {

	@SuppressWarnings("unchecked")
	@Override
	public void notifyStatusChange(List<Object> orderIds, RequestTemplateTypes type) {
		if (orderIds == null || orderIds.isEmpty()) {
			return;
		}
		String reqTypeStr = settingsUtil.getValue("valid_req_types");
		Map<String, List<Integer>>  reqTypeMap  = JsonUtil.readValue(reqTypeStr, Map.class);
		List<Map<String, Object>> emailDtlsList = partnerDao.getPartnerEmailByOrderIds(orderIds);
		List<Integer> orderStatuses = reqTypeMap.get(RequestTemplateTypes.ORDER.name());
		Map<String, Map<String, Object>> inputDtlsMap = new HashMap<String, Map<String, Object>>();
		emailDtlsList.stream().forEach( map -> {
			if(isValidReqStatus((Integer)map.get("statusId"), orderStatuses)) {
				inputDtlsMap.put(String.valueOf(map.get("order_id")), map);
			}
		});
		sendEmail(inputDtlsMap);
	}
}
