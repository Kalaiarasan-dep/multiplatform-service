package in.hashconnect.logging;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

public class SensitiveDataSourceService {

	@Value("${sensitive.logging.filter.regex}")
	private String patterns;

	@PostConstruct
	public void init() {
		SensitiveDataFilter.setPatterns(Arrays.asList(patterns.split(";")));
	}
}
