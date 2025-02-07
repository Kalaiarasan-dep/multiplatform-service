package in.hashconnect.service;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.ServiceFactory;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.util.StringUtil;

@Service
public class InvoiceReqListingPostProcessor implements CommonApiPostProcessor, CommonApiPreProcessor {

	@Autowired
	private ServiceFactory serviceFactory;
	@Autowired
	private PartnerDao partnerDao;

	// post-processor
	@Override
	public Response<?> process(Response<?> response, Map<String, Object> request) {
		Integer invReqId = StringUtil.convertToInt(MapUtils.getString(request, "irid"));
		String role = getString(request,"role");
		request.put("role", role+"_COUNT");
		GetResponse getResp = (GetResponse) response;

	/*	GetResponse totalRecordResp = (GetResponse) serviceFactory.get("get").process("invoice-request", request);
		Map<String, Object> totalRMap = CollectionUtils.isNotEmpty(totalRecordResp.getRecords())? 
				totalRecordResp.getRecords().get(0):Collections.emptyMap();
		getResp.setTotalRecords(MapUtils.getInteger(totalRMap, "total_rows")); */
		
		if (invReqId == null) {
			return response;
		}

		if (!partnerDao.isInvSubmittedByLoggedUser(invReqId, Integer.valueOf(getString(request, "userid")))) {
			response.setStatus(STATUS.SUCCESS);
			String partnerEmail = partnerDao.getPartnerEmailId(String.valueOf(invReqId));
			response.setDesc(" Please login with the registered email ID " + partnerEmail + " to upload the invoice");
			return  response;
		}

		// prepare request
		request.put("invReqIdIn", invReqId);
		request.put("role", role+"_ID");
		GetResponse irResponse = (GetResponse) serviceFactory.get("get").process("invoice-request", request);
		if (irResponse.getStatus() == STATUS.FAILED) {
			return getResp;
		}
		getResp.setData(CollectionUtils.isNotEmpty(irResponse.getRecords())?
				irResponse.getRecords().get(0):null);
		return getResp;
	}

	// pre-processor
	@Override
	public Response<?> process(Map<String, Object> request) {
		Integer invReqId = StringUtil.convertToInt(MapUtils.getString(request, "irid"));
		if (invReqId != null) {
			request.put("invReqIdNotIn", invReqId);
		}

		return Response.ok();
	}
}
