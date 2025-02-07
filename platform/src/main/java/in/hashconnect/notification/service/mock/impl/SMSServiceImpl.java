package in.hashconnect.notification.service.mock.impl;

import java.util.Map;

import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.AbstractNotificationService;
import in.hashconnect.notification.service.vo.Notification;

public class SMSServiceImpl extends AbstractNotificationService implements NotificationService {

	public void process(Notification notification) {
		String template = notification.getTemplate();
		String to = notification.getTo();
		Map<String, Object> reqParams = notification.getParams();
		// save in db
		Long id = persistNotification(template, SMS, to, null, null, null, reqParams);

		try {
			Thread.sleep(1000); // add a 1-second delay
		} catch (InterruptedException e) {
			// handle the exception if needed
		}
		updateStatus(id, "NOT TRIGGERED", null, null);

	}

}
