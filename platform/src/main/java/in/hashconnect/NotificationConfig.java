package in.hashconnect;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections4.MapUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import in.hashconnect.notification.dao.NotificationDao;
import in.hashconnect.notification.dao.NotificationDaoImpl;
import in.hashconnect.notification.properties.EmailGatewayProperties;
import in.hashconnect.notification.properties.SMSRouteProperties;
import in.hashconnect.notification.service.NotificationService;
import in.hashconnect.notification.service.impl.AisensyWhatsAppNotificationServiceImpl;
import in.hashconnect.notification.service.impl.EmailModeService;
import in.hashconnect.notification.service.impl.EmailServiceImpl;
import in.hashconnect.notification.service.impl.HTTPSEmailModeServiceImpl;
import in.hashconnect.notification.service.impl.KarixSMServiceImpl;
import in.hashconnect.notification.service.impl.KarixStatusScheduler;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.impl.SMTPEmailModeServiceImpl;
import in.hashconnect.notification.service.mock.impl.WhatsAppServiceImpl;
import in.hashconnect.util.JsonUtil;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@SuppressWarnings("unchecked")
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "notification.enabled", havingValue = "true", matchIfMissing = false)
public class NotificationConfig {
	private static final Logger logger = LoggerFactory.getLogger(NotificationConfig.class);

	@Bean
	public NotificationDao notificationDao(DataSource dataSource) {
		NotificationDaoImpl impl = new NotificationDaoImpl();
		impl.setDataSource(dataSource);
		return impl;
	}

	@Bean
	public VelocityEngine velocityEnginer(DataSource ds) {
		Properties p = new Properties();
		p.put("ds.resource.loader.instance", getDataSourceResourceLoader(ds));
		p.put("resource.loader", "file, ds");
		p.put("ds.resource.loader.public.name", "DataSource");
		p.put("ds.resource.loader.resource.table", "templates");
		p.put("ds.resource.loader.resource.keycolumn", "name");
		p.put("ds.resource.loader.resource.templatecolumn", "template");
		p.put("ds.resource.loader.resource.timestampcolumn", "modified_time");
		p.put("ds.resource.loader.cache", "false");
		p.put("ds.resource.loader.modification_check_interval", "600");

		return new VelocityEngine(p);
	}

	private DataSourceResourceLoader getDataSourceResourceLoader(DataSource ds) {
		DataSourceResourceLoader dsLoader = new DataSourceResourceLoader();
		dsLoader.setDataSource(ds);
		return dsLoader;
	}

	@Bean
	@ConfigurationProperties("sms.gateway")
	public SMSRouteProperties smsRouteProperties() {
		return new SMSRouteProperties();
	}

	@Bean
	@ConfigurationProperties("email.gateway")
	public EmailGatewayProperties emailGatewayProperties() {
		return new EmailGatewayProperties();
	}

	@Bean
	public NotificationServiceFactory notificationServiceFactory() {
		return new NotificationServiceFactory();
	}

	@Configuration
	@ConditionalOnProperty(value = "notification.type", havingValue = "mock", matchIfMissing = false)
	public class LocalConfiguration {

		@Bean
		public NotificationService emailNotificationService(@Autowired NotificationDao notificationDao,
				@Autowired VelocityEngine velocityEngine) {
			logger.info("MOCK EMAIL service configured");
			return new in.hashconnect.notification.service.mock.impl.EmailServiceImpl();
		}

		@Bean
		public NotificationService smsNotificationService() {
			logger.info("MOCK SMS service configured");
			return new in.hashconnect.notification.service.mock.impl.SMSServiceImpl();
		}

		@Bean
		@ConditionalOnProperty(value = "notification.whatsapp.enabled", havingValue = "true", matchIfMissing = false)
		public NotificationService whatsappNotificationService() {
			logger.info("MOCK Whatsapp service configured");
			return new WhatsAppServiceImpl();
		}
	}

	@Configuration
	@ConditionalOnProperty(value = "notification.type", havingValue = "live", matchIfMissing = false)
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
		public NotificationService emailNotificationService() {
			logger.info("LIVE EMAIL service configured");
			return new EmailServiceImpl();
		}

		@Bean
		public NotificationService smsNotificationService() {
			logger.info("LIVE SMS service configured");
			return new KarixSMServiceImpl();
		}

		@Bean
		@ConditionalOnProperty(value = "notification.whatsapp.enabled", havingValue = "true", matchIfMissing = false)
		public NotificationService whatsappNotificationService() {
			logger.info("LIVE Whatsapp service configured");
			return new AisensyWhatsAppNotificationServiceImpl();
		}

		@ConditionalOnProperty(value = "karix.status.scheduler.enabled", havingValue = "true", matchIfMissing = false)
		@Bean
		public KarixStatusScheduler karixStatusPullScheduler() {
			return new KarixStatusScheduler();
		}
	}

}
