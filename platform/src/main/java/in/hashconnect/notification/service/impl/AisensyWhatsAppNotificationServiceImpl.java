package in.hashconnect.notification.service.impl;

import static org.apache.commons.collections4.MapUtils.getMap;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.http.client.HttpClientFactory;
import in.hashconnect.http.client.HttpClientFactory.TYPE;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.WhatsAppNotification;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;

@SuppressWarnings("unchecked")
public class AisensyWhatsAppNotificationServiceImpl extends AbstractNotificationService implements NotificationService {
	private final Logger logger = LoggerFactory.getLogger(AisensyWhatsAppNotificationServiceImpl.class);

	@Autowired
	private SettingsUtil settingsUtil;

	@Override
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

		Map<String, Object> clientConfig = JsonUtil.readValue(settingsUtil.getValue("whatsapp_aisensy_config"),
				Map.class);

		String url = getString(clientConfig, "url");
		Map<String, String> headers = (Map<String, String>) getMap(clientConfig, "headers");

		String response = HttpClientFactory.get(TYPE.apache).doPost(url, waNotification, headers);

		waNotification.setResponse(JsonUtil.readValue(response, Map.class));

		String status = "FAILED", responseId = null;
		List<Map<String, Object>> messages = (List<Map<String, Object>>) getObject(waNotification.getResponse(),
				"messages");
		if (CollectionUtils.isNotEmpty(messages)) {
			Map<String, Object> message = messages.get(0);

			responseId = getString(message, "id");
			status = getString(message, "message_status");
		}

		updateStatus(id, status, response, responseId);
	}
}
