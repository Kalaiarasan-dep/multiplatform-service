package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.logging.SensitiveDataSourceService;

@Configuration
@ConditionalOnProperty(value = "sensitive.logging.filter.enabled", havingValue = "true", matchIfMissing = false)
public class SensitiveDataLogginConfig {

	@Bean
	public SensitiveDataSourceService sensitiveDataSourceService() {
		return new SensitiveDataSourceService();
	}
}
