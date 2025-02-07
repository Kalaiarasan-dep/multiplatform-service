package in.hashconnect.service.mock.impl;

import in.hashconnect.controller.vo.Notification;
import in.hashconnect.service.NotificationService;
import in.hashconnect.service.impl.AbstractNotificationService;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class SMSNotificationServiceImpl extends AbstractNotificationService implements NotificationService {

	public void process(Notification notification) {
		String template = notification.getTemplate();
		String type = notification.getType().toString();
		String to = notification.getTo();
		Map<String, Object> reqParams = notification.getParams();
		Map<String, Object> params = getTemplateDetailsByKey(template);
		// save in db
		Long id = persistNotification(buildNotificationParams(MapUtils.getInteger(params,"id"), type, to, null, null, null)
				, reqParams, notification.getClientId());

		try {
			Thread.sleep(1000); // add a 1-second delay
		} catch (InterruptedException e) {
			// handle the exception if needed
			Thread.currentThread().interrupt();
		}
		updateStatus(id, "NOT TRIGGERED", null,null);

	}
	@Override
	public void updateStatus() {
		// mock method
	}

}
