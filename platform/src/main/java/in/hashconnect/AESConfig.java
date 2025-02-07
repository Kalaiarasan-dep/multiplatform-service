package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.util.AESUtil;

@Configuration
@ConditionalOnProperty(value = "aes.enabled", havingValue = "true", matchIfMissing = false)
public class AESConfig {

	@Bean
	public AESUtil aesUtil() {
		return new AESUtil();
	}
}
