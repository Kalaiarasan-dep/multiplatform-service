package in.hashconnect.controller;


import in.hashconnect.controller.vo.Notification;
import in.hashconnect.controller.vo.Request;
import in.hashconnect.controller.vo.Response;
import in.hashconnect.service.impl.NotificationServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class NotificationControllerImpl implements NotificationController {

	private static final Logger logger = LoggerFactory.getLogger(NotificationControllerImpl.class);

	@Autowired
	private NotificationServiceFactory notificationFactory;

	@Override
	public Response send(Long tenantId, Request request) {

		logger.info("Notification Request " + request);
		Response response = new Response();

		Notification notification = request.getNotification();
		notification.setClientId(tenantId);
		notificationFactory.get(notification.getType()).process(notification);

		response.setStatus("SUCCESS");

		return response;
	}

	@Override
	public Response karix() {
		Response response = new Response();
		notificationFactory.get(Notification.TYPE.SMS).updateStatus();
		response.setStatus("SUCCESS");

		return response;
	}
}
