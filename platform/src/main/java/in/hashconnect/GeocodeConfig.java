package in.hashconnect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import in.hashconnect.geocode.GeocodeService;
import in.hashconnect.geocode.GoogleGeocodeService;

@Configuration
@ConditionalOnProperty(value = "geocode.enabled", havingValue = "true", matchIfMissing = false)
public class GeocodeConfig {

	@Bean
	@ConditionalOnProperty(value = "geocode.type", havingValue = "google", matchIfMissing = false)
	public GeocodeService geocodeService() {
		return new GoogleGeocodeService();
	}
}
