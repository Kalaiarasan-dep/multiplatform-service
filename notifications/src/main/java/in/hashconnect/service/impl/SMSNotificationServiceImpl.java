package in.hashconnect.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.common.util.StringUtil;
import in.hashconnect.common.vo.DNResponses;
import in.hashconnect.common.vo.KarixStatus;
import in.hashconnect.common.vo.SmsResponse;
import in.hashconnect.config.properties.SMSRouteProperties;
import in.hashconnect.controller.vo.Notification;
import in.hashconnect.controller.vo.Route;
import in.hashconnect.exceptions.CustomException;
import in.hashconnect.service.NotificationService;
import in.hashconnect.util.HttpUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static in.hashconnect.common.util.StringUtil.*;
import static java.net.URLEncoder.encode;
import static org.apache.commons.collections4.MapUtils.getString;

public class SMSNotificationServiceImpl extends AbstractNotificationService implements NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(SMSNotificationServiceImpl.class);
	public static final String UTF_8_CHAR = "utf-8";

	private SMSRouteProperties smsRouteProperties;

	private final XmlMapper xmlMapper = new XmlMapper();
	@Autowired
	private HttpUtil httpUtil;


	public void process(Notification notification) {
		new Thread(() -> asyncProcess(notification)).start();
	}

	public void asyncProcess(Notification notification) {
		logger.info("Processing Notification Request ---- ");

		String template = notification.getTemplate();
		String type = notification.getType().toString();
		String to = notification.getTo();
		Map<String, Object> reqParams = notification.getParams();
		// save in db
		Map<String, Object> dbParams = getTemplateDetailsByKey(template);

		Long id = persistNotification(buildNotificationParams(MapUtils.getInteger(dbParams,"id"), type, to, null, null, null)
				, reqParams, notification.getClientId());

		String route = getString(dbParams, "route");
		if (!isValid(route)) {
			updateStatus(id, "NO ROUTE", null, null);
			return;
		}

		Route r = smsRouteProperties.route(route);
		String billRef = getString(dbParams, "billRef");
		String senderId = getString(dbParams, "sender_id");
		String message = getBody(template, reqParams);
		String dltTemplateId = getString(dbParams, "dlt_template_id");
		String dltEntityId = getString(dbParams, "dlt_entity_id");

		logger.info("route info ---- Ip" + r.getIp() + " version --- " + r.getVersion() + " key --- " + r.getKey());
		String response = send(r, to, message, senderId, billRef, dltTemplateId, dltEntityId);

		String status = StringUtils.isNotBlank(response) && response.startsWith("Request accepted") ? "SUCCESS"
				: "FAILED";

		updateStatus(id, status, response, getRefId(response));
	}

	private String getRefId(String response) {
		try {
			String[] parts = response.split("Request ID=");
			String refid = "";
			if (parts.length > 1) {
				refid = parts[1].split(" ")[0];
			}
			return refid;
		} catch (Exception e) {
			logger.error("Exception while finding refId",e.getMessage());
		}
		return null;
	}

	private String send(Route r, String number, String message, String senderId,
			String billRef, String dltTemplateId, String dltEntityId) {
		String ip = r.getIp();
		String version = r.getVersion();
		String key = r.getKey();

		if (logger.isDebugEnabled())
			logger.debug("send - mobileNo: " + number + ", message: " + message);

		try {
			logger.info("Parameters ----------- " + "version--- " + version + ",num---" + number + ",key---" + key
					+ ",msg---" + message + ",sender--" + senderId);

			String body = concate("ver=", encode(version, UTF_8_CHAR), "&key=", encode(key, UTF_8_CHAR), "&dest=",
					encode(number, UTF_8_CHAR), "&text=", encode(message, UTF_8_CHAR), "&send=", encode(senderId, UTF_8_CHAR),
					"&encrypt=0");
			if (billRef != null)
				body = concate(body, "&cust_ref=", encode(billRef, UTF_8_CHAR));

			if (dltTemplateId != null)
				body = concate(body, "&dlt_template_id=", encode(dltTemplateId, UTF_8_CHAR));

			if (dltEntityId != null)
				body = concate(body, "&dlt_entity_id=", encode(dltEntityId, UTF_8_CHAR));

			if (logger.isDebugEnabled())
				logger.debug("body: " + body);

			String apiResponse = httpUtil.doPost(ip, body);

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

	private String concate(String... values) {
		StringBuilder sb = new StringBuilder();
		for (String s : values) {
			sb.append(s);
		}
		return sb.toString();
	}

	public void setSmsRouteProperties(SMSRouteProperties smsRouteProperties) {
		this.smsRouteProperties = smsRouteProperties;
	}

	public void setHttpUtil(HttpUtil httpUtil) {
		this.httpUtil = httpUtil;
	}

	public SmsResponse getOperatorStatus(String refid) {
		SmsResponse r = new SmsResponse();
		String template = getTemplateDetailsByRefId(refid);
		Map<String, Object> dbParams = getTemplateDetailsByKey(template);
		String route = getString(dbParams, "route");
		Route ro = smsRouteProperties.route(route);
		String status = "SUCCESS";
		try {
			String body = concate("http://dnquery.mgage.solutions/DnQueryHandler/dn", "?ackid=",
					encode(refid, UTF_8_CHAR), "&aid=", encode(ro.getAid(), UTF_8_CHAR), "&pin=",
					encode(ro.getPin(), UTF_8_CHAR));

			String dnResponse = httpUtil.doGet(body);
			r.setResponse(dnResponse);
			r.setStatus(status);
			return r;
		} catch (Exception e) {
			throw new CustomException(e);
		}

	}

	@Override
	public void updateStatus() {
		String str ="3";
		logger.info(str);
		List<KarixStatus> response = getOpearatorStsPendingRecords(str);
		response.parallelStream().forEach(r -> {
			String operatorSts = null;

			String template = r.getTemplate();
			String refId = r.getReference_id();
			try {
				SmsResponse dnResponse = getOperatorStatus(refId);
				logger.info(String.valueOf(dnResponse));
				DNResponses dnResponses = xmlMapper.readValue(dnResponse.getResponse(), DNResponses.class);
				operatorSts = JsonUtil.toString(dnResponses);
				logger.info(operatorSts);
			} catch (Exception e) {
				logger.error("failed to update Notification Status of karix response", e);
				operatorSts = "failed fetch response " + StringUtil.cut(ExceptionUtils.getStackTrace(e), 450);
			} finally {
				updateNotificationStatus(refId, operatorSts);
			}

		});
	}
}