package in.hashconnect.notification.service.mock.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.AbstractNotificationService;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.WhatsAppNotification;
import in.hashconnect.util.JsonUtil;

public class WhatsAppServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

	public void process(Notification notification) {
		WhatsAppNotification waNotification = notification instanceof WhatsAppNotification
				? (WhatsAppNotification) notification
				: null;

		if (waNotification == null) {
			logger.error("invalid request type received, received is of type {}", notification.getClass());

			return;
		}

		Map<String, Object> params = JsonUtil.convert(waNotification, Map.class);

		Long id = persistNotification(waNotification.getWaTemplate().getName(), WHATSAPP, waNotification.getTo(), null,
				null, null, params);

		Map<String,Object> response = new HashMap<>();
		response.put("messagingProduct","whatsapp");
		List<Map<String,Object>> contacts = new ArrayList<>();
		Map<String,Object> contact = new HashMap<>();
		contact.put("input",waNotification.getTo());
		contacts.add(contact);
		response.put("contacts",contacts);

		List<Map<String,Object>> messages = new ArrayList<>();
		Map<String,Object> message = new HashMap<>();
		message.put("id",String.valueOf(System.currentTimeMillis()));
		message.put("message_status","accepted");
		messages.add(message);
		response.put("messages",messages);
		waNotification.setResponse(JsonUtil.convert(response,Map.class));
		// update DB
		updateStatus(id, "NOT TRIGGERED", null, null);

	}
}
