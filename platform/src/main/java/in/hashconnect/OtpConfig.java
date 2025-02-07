package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.otp.OtpDao;
import in.hashconnect.otp.OtpService;
import in.hashconnect.otp.OtpServiceImpl;

@Configuration
@ConditionalOnProperty(value = "otp.enabled", havingValue = "true", matchIfMissing = false)
public class OtpConfig {

	@Bean
	public OtpService otpService() {
		return new OtpServiceImpl();
	}

	@Bean
	public OtpDao otpDao() {
		return new OtpDao();
	}
}
