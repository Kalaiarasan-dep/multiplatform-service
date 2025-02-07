package in.hashconnect.notification.service.impl;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.notification.dao.NotificationDao;

public abstract class AbstractNotificationService {

	@Autowired
	protected NotificationDao notificationDao;

	@Autowired
	private VelocityEngine velocityEngine;

	protected final static String SMS = "SMS";
	protected final static String EMAIL = "EMAIL";
	protected final static String WHATSAPP= "WHATSAPP";

	protected Map<String, Object> getTemplateDetailsByKey(String key) {
		return notificationDao.getTemplateDetailsByKey(key);
	}

	protected Long persistNotification(String template, String type, String to, String from, String cc, String subject,
			Map<String, Object> params) {
		return notificationDao.insertNotification(template, type, to, from, cc, subject, params);
	}

	protected void updateStatus(Long id, String status, String apiResponse, String refId) {
		notificationDao.updateNotification(id, status, apiResponse, refId);
	}

	protected String getBody(String template, Map<String, Object> params) {
		VelocityContext context = new VelocityContext(params);
		StringWriter writer = new StringWriter();

		synchronized (template.intern()) {
			velocityEngine.mergeTemplate(template, "UTF-8", context, writer);
		}
		return writer.toString();
	}
}
