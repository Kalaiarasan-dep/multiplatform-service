package in.hashconnect.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.ServiceFactory;
import in.hashconnect.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;

import static org.apache.commons.collections4.MapUtils.getString;

@Service
public class InvoiceActionPostProcessor implements CommonApiPostProcessor, CommonApiPreProcessor {

    @Autowired
    private ServiceFactory serviceFactory;
	@Autowired
	private GenericDao genericDao;
	@SuppressWarnings("unchecked")
	@Override
	public Response<?> process(Response<?> response, Map<String, Object> request) {
		 if (response.getStatus() == Response.STATUS.FAILED  || response.getRecords().isEmpty()) {
	            return response;
	        }
	     GetResponse resp = (GetResponse) response;
        Integer invReqId = StringUtil.convertToInt(MapUtils.getString(request, "irid"));
        if(invReqId!=null){
            request.put("invReqIdIn", invReqId);
            String role = getString(request,"role");
            request.put("role", role+"_ID");
            GetResponse irResponse = (GetResponse) serviceFactory.get("get").process("invoice-request", request);
            if(irResponse.getStatus() == Response.STATUS.FAILED) {
                return resp;
            }
            resp.setData(CollectionUtils.isNotEmpty(irResponse.getRecords())?
                    irResponse.getRecords().get(0):null);
            return resp;
        }

	     if (request.containsKey("statusIn") && request.containsKey("programIn") && request.containsKey("monthIn") && (request.containsKey("partnerIn")|| request.containsKey("partnerSelectAll"))) {

             List<String> statusList = (List<String>) request.get("statusIn");
             List<String> programList = (List<String>) request.get("programIn");
             List<String> monthList = (List<String>) request.get("monthIn");
             List<String> partnerList = null;
             if(request.containsKey("partnerIn")) {
                 partnerList = (List<String>) request.get("partnerIn");
             }

             if (statusList.size() == 1 && programList.size() == 1 && monthList.size() == 1 && (request.containsKey("partnerSelectAll") || partnerList.size() == 1 )) {
                 for (String statusId : statusList) {
                   List<Map<String, Object>> actionResult = genericDao.getAction(statusId,"INVOICE_REQUEST");
                     if (actionResult instanceof List) {
                         List<Map<String, Object>> resultList = (List<Map<String, Object>>) actionResult;
                         Map<String, Object> actions = new HashMap<>();
                         actions.put("actions", resultList);
                         resp.setData(actions);
                   }

                 }
             }

     }
	     
		return resp;
	}
    @Override
    public Response<?> process(Map<String, Object> request) {
        Integer invReqId = StringUtil.convertToInt(MapUtils.getString(request, "irid"));
        if (invReqId != null) {
            request.put("invReqIdNotIn", invReqId);
        }

        return Response.ok();
    }

}
