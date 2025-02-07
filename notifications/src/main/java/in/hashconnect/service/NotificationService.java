package in.hashconnect.service;


import in.hashconnect.controller.vo.Notification;

public interface NotificationService {

	void process(Notification notification);

	void updateStatus();
}
