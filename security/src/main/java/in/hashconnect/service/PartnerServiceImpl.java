package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.util.BillsVo;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SOASummary;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.vo.PartnerInvoiceValidator;
import in.hashconnect.vo.PartnerResponse;
import in.hashconnect.vo.RequestTemplateTypes;

@Service
public class PartnerServiceImpl implements PartnerService{
	private static final Logger logger = LoggerFactory.getLogger(PartnerServiceImpl.class);
	@Autowired
	private PartnerInvoiceValidator validator;
	@Autowired
	private GenericDao genericDao;
	@Autowired
	private PartnerDao partnerDao;
	@Autowired
	private SettingsUtil settingsUtil;
	@Autowired
	private ReqStatusChangeNotifyService reqStatusChangeNotifyService;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private NotificationServiceFactory notificationServiceFactory;
	private static final int CHUNK_SIZE = 5 * 1024 * 1024;

	@Override
	public PartnerResponse validatePartner(Map<String, Object> invoiceDtls) {
		return validator.validate(invoiceDtls);
		
	}
	@Override
	public void submitInvoice(Map<String, Object> invoiceDtls) {
		String status=getString(invoiceDtls,"status");
		if("Invoice Onhold".equalsIgnoreCase(status))
			genericDao.reuploadInvoice(invoiceDtls);
		else
			genericDao.updateInvoicebyRequestId(invoiceDtls);

		partnerDao.updateOrderOnInvSubmission(getInteger(invoiceDtls, "invReqId"));

	}
	
	@Override
	public Map<String, Object> getPartnerInvoiceDtls(String invId) {
		Map<String, Object> partnerDtls = partnerDao.getPartnerInvoiceDtls(invId);
		if (partnerDtls.isEmpty()) {
			return partnerDtls;
		}
		String inv_image_api_url =  settingsUtil.getValue("inv_image_api_url");
		inv_image_api_url = StringUtil.concate(inv_image_api_url,invId,"&type=",getString(partnerDtls, "content_type"));
		partnerDtls.put("inv_image_api_url", inv_image_api_url);
				
		return partnerDtls;
	}
	
	
	@Override
	public boolean saveInvValidation(Map<String, Object> resp) {
		try {
			String jsonData = JsonUtil.toString(resp);
			String invId = getString(resp, "docNo");
			String actionName = getString(resp, "selectedActionName");
			Integer actionId = getInteger(resp, "selectedActionId");
			String invoiceNumber = getString(resp, "inv_number");
			String invoiceDate = getString(resp, "inv_date");
			String reasons = getString(resp, "status_reasons");
			boolean updateInvDateNumber = StringUtil.isValid(invoiceNumber) || StringUtil.isValid(invoiceDate);
			Integer statusId = partnerDao.getStatusIdByAction(actionName, actionId);
			String infoMsg = StringUtil.concate("invId=",invId,"actionName = ", actionName, "actionId = ",actionId,
					"invoiceNumber=",invoiceNumber,"invoiceDate=",invoiceDate,"statusId=",statusId);
			logger.info(infoMsg);
			resp.put("msg", jsonData);
			resp.put("invId", invId);
			resp.put("statusId", statusId);
			partnerDao.saveInvValidation(resp);
			if (updateInvDateNumber) {
				partnerDao.updateInvoiceDateAndInvNumberByInvReqId(invId, invoiceDate, invoiceNumber);
			}
			partnerDao.updateInvReqStatus(statusId , Integer.valueOf(invId));
			partnerDao.updateOrderByInvIdStatus(statusId , Integer.valueOf(invId));
			if (StringUtil.isValid(reasons)) {
				partnerDao.updateInvReqRemarks(reasons, invId);
			}
			if ("Approve".equalsIgnoreCase(actionName)) {
				logger.info("Invoice approval. inv no: {} and inv id: {}", invoiceNumber, invId);
				partnerDao.updateInvZohoPushStatus(invoiceNumber, invId);
			}
			reqStatusChangeNotifyService.notifyStatusChange(Arrays.asList(invId), 
					RequestTemplateTypes.INVOICE);
			return true;
		} catch(Exception e) {
			logger.error("Exception occurred while saving inv validation dtls", e);
			return false;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BillsVo> getBillReport(String partnerId, SOASummary summary) {
		List<BillsVo> bills = partnerDao.getBillReport(partnerId);
		String excludeStatus = settingsUtil.getValue("soa_exclude_status");
		List<String> EXCLUDE_STATUSES_FOR_TOTAL = JsonUtil.readValue(excludeStatus, List.class);
		
		 
		// populate values
		bills.stream().forEach(b -> {
			List<Map<String, Object>> paymentDetails = JsonUtil.readValue(b.getPayments(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			StringBuilder payDetailsInReport = new StringBuilder();
			paymentDetails.stream().forEach(pd -> {
				if (payDetailsInReport.length() > 0)
					payDetailsInReport.append(",");

				String paidThrough = getString(pd, "paid_through");
				String paidThroughActName = getString(pd, "paid_through_account_name");

				paidThrough = StringUtil.isValid(paidThrough) ? paidThrough
						: StringUtil.isValid(paidThroughActName) ? paidThroughActName : StringUtil.EMPTY;

				payDetailsInReport.append("[UTR:").append(getString(pd, "reference_number")).append(", Payment Date:")
						.append(getString(pd, "date")).append(", Bank:").append(paidThrough)
						.append(", Total Payment Against UTR:").append(getString(pd, "amount")).append("]");
			});
			b.setPaySummary(payDetailsInReport.toString());

			BigDecimal totalPaid = b.getSubTotal().add(b.getTaxTotal());
			b.setTotalWithOutTds(totalPaid);

			if (!EXCLUDE_STATUSES_FOR_TOTAL.contains(b.getStatus()))
				summary.appendTotalOfSoa(totalPaid);

			b.setBookTime(formatDate(b.getBookTime()));
		});

		return bills;
	}
	private String formatDate(String date) {
		LocalDate originalDate = LocalDate.parse(date);
        DateTimeFormatter desiredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = originalDate.format(desiredFormat);
		
        return formattedDate == null ? StringUtil.EMPTY : formattedDate;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPaymentDtlsByInvId(Integer invId) {
		String jsonStr = partnerDao.getPaymentDtlsByInvId(invId);
		List<Map<String, Object>> list = JsonUtil.readValue(jsonStr, List.class);
		return CollectionUtils.isNotEmpty(list)?list.get(0):Collections.EMPTY_MAP;
	}
	@Override
	public Map<String, Object> getPartnerGSTAndBankDetailsByInvNo(String invId) {
		return partnerDao.getPartnerGSTAndBankDetailsByInvNo(invId);
	}
	@Override
	public Map<String, String> getPartnerBannerDtls(String userId) {
		Map<String, Object> partnerMap = partnerDao.getPartnerId(userId);
		boolean firstLogin = partnerDao.isPartnerLogInFirstTime(userId);
		if (!firstLogin) {
			return Collections.emptyMap();
		}
		String partnerId = MapUtils.getString(partnerMap, "partner_id");
		return partnerDao.getPartnerBannerDtls(partnerId, userId);
	}
	@Override
	public String getUserRoleByUserId(Integer userId) {
		return partnerDao.getRoleByUserId(userId);
	}

	@Override
	public void savePartnerStatus(Long userId,Integer statusId,Long partnerId,String type) {
		 partnerDao.savePartnerStatus(userId,statusId,partnerId,type);
	}

	@Override
	public Map<String, Object> getPartnerValidationDtls(String userId) {
		Map<String, Object> partnerDtls = partnerDao.getPartnerValidationDtls(userId);
		return partnerDtls;
	}

	@Override
	public boolean saveSignUpValidation(Map<String, Object> resp) {
		try {
			String jsonData = JsonUtil.toString(resp);
			String userId = getString(resp, "docNo");
			String actionName = getString(resp, "selectedActionName");
			List<Map<String, Object>> reasonsList = (List<Map<String, Object>>) resp.get("reasons");
			List<String> reasons = reasonsList.stream()
					.map(reason -> (String) reason.get("name"))
					.collect(Collectors.toList());

			Integer statusId = partnerDao.getStatusId(actionName);
			resp.put("msg", jsonData);
			resp.put("userId", userId);
			resp.put("statusId", statusId);
			partnerDao.saveSignUpValidation(resp);
			partnerDao.updatePartnerStatus(statusId , Integer.valueOf(userId));
			Map<String, Object> params = new HashMap<>(3);
			String templateName = null;
			if(actionName.equalsIgnoreCase("Approve")){
				params.put("subject", settingsUtil.getValue("signUp_approved_template_subject"));
				params.put("link", settingsUtil.getValue("login_link"));
				params.put("imageLink", settingsUtil.getValue("signup_email_image_link"));
				templateName=settingsUtil.getValue("sign_up_approved_template");
				partnerDao.insertToUserPerformsRole(Integer.valueOf(userId));

			}
			if(actionName.equalsIgnoreCase("On Hold")){
				params.put("subject", settingsUtil.getValue("signUp_onHold_template_subject"));
				params.put("reasons",reasons);
				templateName=settingsUtil.getValue("sign_up_onHold_template");
			}

			if(actionName.equalsIgnoreCase("Rejected")){
				params.put("subject", settingsUtil.getValue("signUp_rejected_template_subject"));
				templateName=settingsUtil.getValue("sign_up_rejected_template");
				params.put("reasons",reasons);
			}
			if(actionName.equalsIgnoreCase("Escalated")){
				params.put("subject", settingsUtil.getValue("signUp_escalated_template_subject"));
				templateName=settingsUtil.getValue("sign_up_escalated_template");
				params.put("reasons",reasons);
			}
			Map<String, Object> partnerDetails = partnerDao.getPartnerInfoById(Integer.valueOf(userId));
			params.put("registered_business_name",getString(partnerDetails, "registered_business_name"));
			params.put("owner_name",getString(partnerDetails, "owner_name"));

			notificationServiceFactory.get(Notification.TYPE.EMAIL).process(new Notification(
					templateName, getString(partnerDetails, "owner_email"),
					getString(params, "subject"), params));
			return true;
		} catch(Exception e) {
			logger.error("Error while saving signup validation dtls ", e);
			return false;
		}

	}

	@Override
	public List<String> getPartnerList() {
		return partnerDao.getPartnerList();
	}
	
}
