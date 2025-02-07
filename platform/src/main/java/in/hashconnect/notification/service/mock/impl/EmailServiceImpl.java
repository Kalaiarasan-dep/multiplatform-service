package in.hashconnect.notification.service.mock.impl;

import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.AbstractNotificationService;
import in.hashconnect.notification.service.vo.Notification;

public class EmailServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	public void process(Notification notification) {
		String template = notification.getTemplate();
		String to = notification.getTo();
		String cc = notification.getCc();
		Map<String, Object> reqParams = notification.getParams();
		// get details by key
		Map<String, Object> params = getTemplateDetailsByKey(template);
		if (params == null) {
			logger.info("invalid temlate {}", template);
			return;
		}

		String subject = notification.getSubject();
		if (!isValid(subject))
			subject = getString(params, "subject");

		String from = notification.getFrom();
		if (!isValid(from))
			from = getString(params, "efrom");

		// save in db
		Long id = persistNotification(template, EMAIL, to, from, cc, subject, reqParams);

		// update DB
		updateStatus(id, "NOT TRIGGERED", null, null);

	}
}
