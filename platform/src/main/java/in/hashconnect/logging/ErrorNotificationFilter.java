package in.hashconnect.logging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.util.DateUtil;

@Plugin(name = "ErrorNotificationFilter", category = "Core", elementType = "filter", printObject = true)
public class ErrorNotificationFilter extends AbstractFilter implements ApplicationContextAware {
	private static ErrorNotificationFilter filter = null;

	private NotificationServiceFactory notificationFactory;
	private String appName;
	private String publishTo;

	@Override
	public Result filter(LogEvent event) {
		if (notificationFactory == null)
			return Result.NEUTRAL;

		String level = event.getLevel().name();

		System.out.println("coming here now");

		if ("error".equalsIgnoreCase(level)) {
			String message = event.getMessage().getFormattedMessage();
			String createdDate = DateUtil.format("dd-MM-yyyy hh:mm:ss", new Date(event.getTimeMillis()));

			Map<String, Object> params = new HashMap<>();
			params.put("error", message);
			params.put("created_date", createdDate);
			params.put("type", appName);

			System.out.println("sending email now");

			notificationFactory.get(TYPE.EMAIL).process(new Notification("app_error", publishTo, params));
		}

		return Result.NEUTRAL;
	}

	@PluginFactory
	public static ErrorNotificationFilter createFilter() {
		filter = new ErrorNotificationFilter();
		return filter;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (filter == null)
			return;
		filter.notificationFactory = applicationContext.getBean(NotificationServiceFactory.class);
		filter.publishTo = applicationContext.getEnvironment().getProperty("error.publish.to");
		filter.appName = applicationContext.getEnvironment().getProperty("spring.application.name");
	}
}
