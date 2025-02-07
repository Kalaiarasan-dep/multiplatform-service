package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.feign.client.AuthProxy;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.vo.TokenCreateRequest;

public abstract class AbstractEmailService {
	
	@Autowired
	private NotificationServiceFactory notificationServiceFactory;
	@Autowired
	public PartnerDao partnerDao;
	@Autowired
	public SettingsUtil settingsUtil;
	@Autowired
	private AuthProxy authProxy;
	private static final String EMAIL_TEMPLATE_INV_REQUESTED = "inv_req_change";
	private static final String EMAIL_TEMPLATE_INV_ONHOLD_OTHERS = "inv_onhold_others";
	
	public boolean isValidReqStatus(Integer statusId, List<Integer> statuses) {
		return statuses.contains(statusId);
	}
	
	public void sendEmail(Map<String, Map<String, Object>> inputDtlsMap) {
		//create a task and send email
		inputDtlsMap.entrySet().stream().forEach(triggerDtls ->
				CompletableFuture.runAsync(() -> triggerNotification(triggerDtls.getKey(), triggerDtls.getValue())));
	}
	
	private void triggerNotification(String id, Map<String, Object> emailDtls) {
		NotificationService notificationService =  notificationServiceFactory.get(TYPE.EMAIL);
		
			emailDtls.put("inv_url", getFormattedUrl(getString(emailDtls, "id")));
			notificationService.process(new Notification(getString(emailDtls, "template"), 
					getString(emailDtls, "owner_email"), getString(emailDtls, "subject"), emailDtls)); 
	}
	
	public String getTemplateTye(Integer statusId) {
		Integer inv_requested_status = settingsUtil.getIntValue("inv_requested_status");
		List<Integer> INV_REQUESTED_ID = Arrays.asList(inv_requested_status!= null ?
				inv_requested_status:0);
		
		if (INV_REQUESTED_ID.contains(statusId)) {
			return EMAIL_TEMPLATE_INV_REQUESTED;
		}
		return EMAIL_TEMPLATE_INV_ONHOLD_OTHERS;
	}
	
	private String getFormattedUrl(String invId) {
		return StringUtil.concate(settingsUtil.getValue("inv_url"), "&irid=", invId);
	}

}
