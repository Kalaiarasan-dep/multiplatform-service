package in.hashconnect.service.mock.impl;


import in.hashconnect.controller.vo.Notification;
import in.hashconnect.service.NotificationService;
import in.hashconnect.service.impl.AbstractNotificationService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.lang.StringUtils.isBlank;

public class EmailNotificationServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

	@Override
	public void process(Notification notification) {
		String template = notification.getTemplate();
		String type = notification.getType().toString();
		String to = notification.getTo();
		String cc = notification.getCc();
		String subject = notification.getSubject();

		Map<String, Object> reqParams = notification.getParams();
		// get details by key
		Map<String, Object> params = getTemplateDetailsByKey(template);
		if (params == null) {
			logger.info("invalid temlate {}", template);
			return;
		}

		if (isBlank(subject)){
			subject = getString(params, "subject");
		}
		String from = getString(params, "efrom");

		// save in db
		Long id = persistNotification(buildNotificationParams(MapUtils.getInteger(params,"id") ,type, to, from, cc, subject)
				, reqParams, notification.getClientId());

		// update DB
		updateStatus(id, "NOT TRIGGERED", null,null);
	}
}
