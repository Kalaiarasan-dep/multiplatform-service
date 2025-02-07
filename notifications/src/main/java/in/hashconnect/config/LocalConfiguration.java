package in.hashconnect.config;

import in.hashconnect.dao.GenericDao;
import in.hashconnect.service.NotificationService;
import in.hashconnect.service.mock.impl.EmailNotificationServiceImpl;
import in.hashconnect.service.mock.impl.SMSNotificationServiceImpl;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "dev" })
public class LocalConfiguration {

	@Bean
	public NotificationService emailNotificationService(@Autowired GenericDao genericDao,
			@Autowired VelocityEngine velocityEngine) {
		EmailNotificationServiceImpl impl = new EmailNotificationServiceImpl();
		impl.setGenericDao(genericDao);
		impl.setVelocityEngine(velocityEngine);
		return impl;
	}

	@Bean
	public NotificationService smsNotificationService(@Autowired GenericDao genericDao,
			@Autowired VelocityEngine velocityEngine) {
		SMSNotificationServiceImpl impl = new SMSNotificationServiceImpl();
		impl.setGenericDao(genericDao);
		impl.setVelocityEngine(velocityEngine);
		return impl;
	}


}
