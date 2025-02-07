package in.hashconnect.notification.service;

import in.hashconnect.notification.service.vo.Notification;

public interface NotificationService {

	void process(Notification notification);
}
