package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.logging.ErrorNotificationFilter;

@Configuration
@ConditionalOnProperty(value = "error.publish.logging.filter.enabled", havingValue = "true", matchIfMissing = false)
public class ErrorPublishLogginConfig {

	@Bean
	public ErrorNotificationFilter errorNotificationFilter() {
		return new ErrorNotificationFilter();
	}
}
