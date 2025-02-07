package in.hashconnect.notification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.vo.Notification;
import org.springframework.context.annotation.Lazy;

public class NotificationServiceFactory {

	@Autowired
	private NotificationService emailNotificationService;

	@Autowired
	private NotificationService smsNotificationService;
	@Autowired
	@Lazy
	private NotificationService whatsappNotificationService
	;
	public NotificationService get(Notification.TYPE type) {
		switch (type) {
		case SMS:
			return smsNotificationService;
		case EMAIL:
			return emailNotificationService;
		case WHATSAPP:
			return whatsappNotificationService;
		}

		throw new RuntimeException("valid notification service not found");
	}
}
