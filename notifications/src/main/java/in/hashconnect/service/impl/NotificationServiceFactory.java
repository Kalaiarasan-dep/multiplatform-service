package in.hashconnect.service.impl;

import in.hashconnect.controller.vo.Notification;
import in.hashconnect.exceptions.CustomException;
import in.hashconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceFactory {

	@Autowired
	private NotificationService emailNotificationService;

	@Autowired
	private NotificationService smsNotificationService;


	public NotificationService get(Notification.TYPE type) {
		switch (type) {
		case SMS:
			return smsNotificationService;
		case EMAIL:
			return emailNotificationService;
		}

		throw new CustomException("valid notification service not found");
	}
}
