package in.hashconnect.notification.service.impl;

import static in.hashconnect.util.StringUtil.concate;
import static java.net.URLEncoder.encode;
import static org.apache.commons.collections4.MapUtils.getLong;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import in.hashconnect.http.client.HttpClientFactory;
import in.hashconnect.http.client.HttpClientFactory.TYPE;
import in.hashconnect.notification.dao.NotificationDao;
import in.hashconnect.notification.properties.SMSRouteProperties;
import in.hashconnect.notification.service.vo.DNResponses;
import in.hashconnect.notification.service.vo.Route;
import in.hashconnect.util.JsonUtil;

public class KarixStatusScheduler {
	private static final Logger logger = LoggerFactory.getLogger(KarixStatusScheduler.class);

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private SMSRouteProperties smsRouteProperties;

	private XmlMapper mapper = new XmlMapper();

	@Scheduled(cron = "0/10 * * * * *")
	public void process() {
		System.out.println("now is " + new Date());
		logger.debug("started");

		List<Map<String, Object>> list = notificationDao.getPendingNotification();
		if (!list.isEmpty()) {
			logger.debug("nothing to process");
			return;
		}
		list.parallelStream().forEach(n -> {
			Long id = getLong(n, "id");
			String refId = getString(n, "reference_id");
			String template = getString(n, "template");

			Map<String, Object> dbParams = notificationDao.getTemplateDetailsByKey(template);
			String route = getString(dbParams, "route");

			Route r = smsRouteProperties.route(route);
			String status = getOperatorStatus(r, refId);

			notificationDao.updateOperatorStatus(id, status);
		});

		logger.debug("finished");
	}

	private String getOperatorStatus(Route route, String refId) {
		try {
			String body = concate(route.getStatusUrl(), "?ackid=", encode(refId, "utf-8"), "&aid=",
					encode(route.getAid(), "utf-8"), "&pin=", encode(route.getPin(), "utf-8"));

			String response = HttpClientFactory.get(TYPE.sun).doGet(body);
			DNResponses dnResponse = mapper.readValue(response, DNResponses.class);

			return JsonUtil.toString(dnResponse);
		} catch (Exception e) {
			return ExceptionUtils.getStackTrace(e);
		}
	}

}
