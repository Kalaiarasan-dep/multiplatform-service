package in.hashconnect.config;


import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.config.properties.EmailGatewayProperties;
import in.hashconnect.config.properties.SMSRouteProperties;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.service.EmailModeService;
import in.hashconnect.service.NotificationService;
import in.hashconnect.service.impl.*;
import in.hashconnect.util.HttpUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

import java.util.Map;

@Configuration
@Profile({ "prod" })
public class ProdConfiguration {

	@Bean
	@ConditionalOnProperty(value = "notification.mode", havingValue = "https", matchIfMissing = false)
	public SesClient sesClient(EmailGatewayProperties gatewayProperties) {
		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(gatewayProperties.getUn(),
				gatewayProperties.getPw());

		Map<String, Object> params = JsonUtil.readValue(gatewayProperties.getParams(), Map.class);

		// Create the SES client
		return SesClient.builder().region(Region.of(MapUtils.getString(params, "region")))
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}

	@Bean
	@ConditionalOnProperty(value = "notification.mode", havingValue = "https", matchIfMissing = false)
	public EmailModeService httpsEmailModeService() {
		return new HTTPSEmailModeServiceImpl();
	}

	@Bean
	@ConditionalOnProperty(value = "notification.mode", havingValue = "smtp", matchIfMissing = true)
	public EmailModeService smtpEmailModeService() {
		return new SMTPEmailModeServiceImpl();
	}

	@Bean
	public NotificationService emailNotificationService(@Autowired EmailGatewayProperties emailGatewayProperties,
			@Autowired GenericDao genericDao, @Autowired VelocityEngine velocityEngine) {

		EmailNotificationServiceImpl impl = new EmailNotificationServiceImpl();
		impl.setGenericDao(genericDao);
		impl.setVelocityEngine(velocityEngine);
		impl.setGatewayProperties(emailGatewayProperties);

		return impl;
	}

	@Bean
	public NotificationService smsNotificationService(@Autowired SMSRouteProperties smsRouteProperties,
			@Autowired GenericDao genericDao, @Autowired VelocityEngine velocityEngine, @Autowired HttpUtil httpUtil) {
		SMSNotificationServiceImpl impl = new SMSNotificationServiceImpl();
		impl.setGenericDao(genericDao);
		impl.setVelocityEngine(velocityEngine);
		impl.setSmsRouteProperties(smsRouteProperties);
		impl.setHttpUtil(httpUtil);
		return impl;
	}


}
