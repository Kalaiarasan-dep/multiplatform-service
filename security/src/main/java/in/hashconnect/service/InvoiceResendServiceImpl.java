package in.hashconnect.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.util.InvoiceResendDetails;

@Service
public class InvoiceResendServiceImpl implements InvoiceResendService{
	
	@Autowired
	private PartnerDao partnerDao;
	@Autowired
	private NotificationServiceFactory notificationServiceFactory;

	private static final String EMAIL_TEMPLATE = "inv_resend";

	@Override
	public Response<List<Integer>> invResend(List<Integer> invIds) {
		Response<List<Integer>> resp = Response.ok();
		List<Integer> validIds = partnerDao.checkInvExist(invIds);
		
		//check if there is any invoice ids does not exist in db. if it is return error resp
		List<Integer> inValidIds = invIds.stream().filter(e -> !validIds.contains(e))
				.collect(Collectors.toList());	
		if (inValidIds.size() > 0) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("Invoice ids does not exist");
			resp.setData(inValidIds);
			return resp;
		}
		List<InvoiceResendDetails> resendList = partnerDao.getPartnersEmail(validIds);
		
		//create a task to send email
		List<CompletableFuture<Void>> futuresList = resendList.stream().map(invResendDtl -> 
		CompletableFuture.runAsync(() -> sendEmail(invResendDtl))).collect(Collectors.toList());
		//execute a task
		futuresList.stream().map(CompletableFuture::join).collect(Collectors.toList());
		
		resp.setStatus(STATUS.SUCCESS);
		return resp;
	}
	
	private void sendEmail(InvoiceResendDetails dtls) {
		NotificationService notificationService =  notificationServiceFactory.get(TYPE.EMAIL);
	
		Map<String, Object> params = new HashMap<>();
		params.put("partner", dtls.partnerName());
		params.put("invid", dtls.invId());

		notificationService.process(new Notification(EMAIL_TEMPLATE, dtls.partnerEmail(), params)); 
	
	}

}
