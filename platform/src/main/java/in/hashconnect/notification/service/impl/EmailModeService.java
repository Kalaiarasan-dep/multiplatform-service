package in.hashconnect.notification.service.impl;

import java.util.Map;

import javax.mail.Message;

public interface EmailModeService {
	Map<String, String> send(Message message) throws Exception;
}
