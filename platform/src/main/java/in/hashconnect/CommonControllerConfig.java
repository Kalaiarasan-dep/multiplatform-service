package in.hashconnect;

import static org.apache.commons.collections4.MapUtils.getString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import in.hashconnect.api.CommonService;
import in.hashconnect.api.CreateService;
import in.hashconnect.api.GetService;
import in.hashconnect.api.ServiceFactory;
import in.hashconnect.api.UpdateService;
import in.hashconnect.api.dao.ApiDao;
import in.hashconnect.api.dao.ApiDaoImpl;
import in.hashconnect.api.exception.MissingTokenException;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.PlatformJsonUtil;
import in.hashconnect.util.StringUtil;

@SuppressWarnings("unchecked")
@Configuration
@ConditionalOnProperty(value = "common.controller.enabled", havingValue = "true", matchIfMissing = false)
public class CommonControllerConfig {
	private static final Logger logger = LoggerFactory.getLogger(CommonControllerConfig.class);

	@Bean
	public CommonService createService() {
		logger.info("common POST service created");
		return new CreateService();
	}

	@Bean
	public CommonService getService() {
		logger.info("common GET service created");
		return new GetService();
	}

	@Bean
	public CommonService updateService() {
		logger.info("common PUT service created");
		return new UpdateService();
	}

	@Bean
	public ServiceFactory serviceFactory() {
		return new ServiceFactory();
	}

	@Bean
	public ApiDao apiDao() {
		return new ApiDaoImpl();
	}

	@Component
	@Aspect
	@ConditionalOnProperty(value = "common.controller.validate.apis", havingValue = "true", matchIfMissing = false)
	public class Validator {

		@Autowired
		private ResourcePatternResolver resourceResolver;

		private Map<String, Object> apiPrivileges;

		@PostConstruct
		public void init() {
			try (InputStream in = resourceResolver.getResource("classpath*:apis/common-api-privileges.json")
					.getInputStream()) {
				apiPrivileges = JsonUtil.readValue(in, Map.class);
			} catch (IOException e) {
				throw new RuntimeException("failed to read apis/common-api-privileges.json file", e);
			}
		}

		@Before("execution(* in.hashconnect.api.CommonController.*(..))")
		public void validateCLMSUser(JoinPoint joinPoint) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();

			String userPriv = request.getParameter("PRIVILEGES");
			String apiPrivs = getString(apiPrivileges, request.getRequestURI());

			if (!StringUtil.isValid(apiPrivs)) {
				return;
			}

			if (Collections.disjoint(Arrays.asList(userPriv.split(",")), Arrays.asList(apiPrivs.split(",")))) {
				throw new MissingTokenException("API priv does not match user priv");
			}
		}

	}

	@Configuration
	@ConditionalOnProperty(value = "common.controller.source", havingValue = "file", matchIfMissing = false)
	public class FileBasedApisConfig {
		@Autowired
		private ResourcePatternResolver resourceResolver;

		@Bean
		public Map<String, Map<String, Object>> apis() {
			Map<String, Map<String, Object>> mapQueries = new HashMap<>();
			try {
				Arrays.asList(resourceResolver.getResources("classpath*:apis/*.json")).forEach(r -> {
					try {
						String api = r.getFilename().replace(".json", "");
						mapQueries.put(api, PlatformJsonUtil.readValue(r.getInputStream(), Map.class));
					} catch (IOException e) {
						throw new RuntimeException("failed to load queries");
					}
				});

			} catch (IOException e) {
				throw new RuntimeException("failed to load queries");
			}
			return mapQueries;
		}
	}

	@Configuration
	@ConditionalOnProperty(value = "common.controller.source", havingValue = "db", matchIfMissing = true)
	public class DBBasedApisConfig {

		@Bean
		public Map<String, Map<String, Object>> apis() {
			return Collections.emptyMap();
		}
	}

}
