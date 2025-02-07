package in.hashconnect.notification.service.impl;

import static in.hashconnect.util.StringUtil.concate;
import static java.net.URLEncoder.encode;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.http.client.HttpClientFactory;
import in.hashconnect.http.client.HttpClientFactory.TYPE;
import in.hashconnect.notification.properties.SMSRouteProperties;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Route;

public class KarixSMServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(KarixSMServiceImpl.class);

	@Autowired
	private SMSRouteProperties smsRouteProperties;

	private final Pattern pattern = Pattern.compile("\\b\\d{19}\\b");

	public void process(Notification notification) {
		new Thread(() -> asyncProcess(notification)).start();
	}

	private void asyncProcess(Notification notification) {
		logger.info("Processing Notification Request ---- ");

		String template = notification.getTemplate();
		String to = notification.getTo();
		Map<String, Object> reqParams = notification.getParams();
		// save in db
		Long id = persistNotification(template, SMS, to, null, null, null, reqParams);

		Map<String, Object> dbParams = getTemplateDetailsByKey(template);
		String route = getString(dbParams, "route");

		Route r = smsRouteProperties.route(route);
		String billRef = getString(dbParams, "billRef");
		String senderId = getString(dbParams, "sender_id");
		String message = getBody(template, reqParams);
		String dltTemplateId = getString(dbParams, "dlt_template_id");
		String dltEntityId = getString(dbParams, "dlt_entity_id");

		logger.info("route info ---- Ip" + r.getIp() + " version --- " + r.getVersion() + " key --- " + r.getKey());
		String response = send(r.getIp(), to, message, r.getVersion(), r.getKey(), senderId, billRef, dltTemplateId,
				dltEntityId);

		String status = StringUtils.isNotBlank(response) && response.startsWith("Request accepted") ? "SUCCESS"
				: "FAILED";

		String refId = findRefId(response);

		updateStatus(id, status, response, refId);
	}

	private String send(String ip, String number, String message, String version, String key, String senderId,
			String billRef, String dltTemplateId, String dltEntityId) {

		if (logger.isDebugEnabled())
			logger.debug("send - mobileNo: " + number + ", message: " + message);

		try {
			logger.debug("Parameters ----------- " + "version--- " + version + ",num---" + number + ",key---" + key
					+ ",sender--" + senderId);

			String body = concate("ver=", encode(version, "utf-8"), "&key=", encode(key, "utf-8"), "&dest=",
					encode(number, "utf-8"), "&text=", encode(message, "utf-8"), "&send=", encode(senderId, "utf-8"),
					"&encrypt=0");
			if (billRef != null)
				body = concate(body, "&cust_ref=", encode(billRef, "utf-8"));
			if (dltTemplateId != null)
				body = concate(body, "&dlt_template_id=", encode(dltTemplateId, "utf-8"));

			if (dltEntityId != null)
				body = concate(body, "&dlt_entity_id=", encode(dltEntityId, "utf-8"));

			if (logger.isDebugEnabled())
				logger.debug("body: " + body);

			String apiResponse = HttpClientFactory.get(TYPE.sun).doPost(ip, body);

			logger.debug("mobile: " + number + " response: " + apiResponse);

			return apiResponse;
		} catch (Exception e) {
			logger.error("failed to send SMS due to " + e.getMessage(), e);
			String ex = ExceptionUtils.getStackTrace(e);

			if (ex.length() > 1000) {
				ex = ex.substring(0, 800);
			}
			return ex;
		}
	}

	private String findRefId(String response) {
		Matcher matcher = pattern.matcher(response);
		if (matcher.find())
			return matcher.group();
		return null;
	}
}
