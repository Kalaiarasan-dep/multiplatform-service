package in.hashconnect;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.util.SettingsUtil;

@Configuration
@ConditionalOnProperty(value = "settings.enabled", havingValue = "true", matchIfMissing = false)
public class SettingsConfig {

	@Bean
	public SettingsUtil settingsUtil(DataSource dataSource) {
		return new SettingsUtil();
	}

}
