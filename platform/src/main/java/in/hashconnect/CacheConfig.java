package in.hashconnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import in.hashconnect.cache.Cache;
import in.hashconnect.cache.LocalCache;
import in.hashconnect.cache.RedisCache;

@SuppressWarnings("rawtypes")
@Configuration
@ConditionalOnProperty(value = "cache.enabled", havingValue = "true", matchIfMissing = false)
public class CacheConfig {
	private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

	@Bean
	@ConditionalOnProperty(value = "cache.type", havingValue = "map", matchIfMissing = false)
	public Cache localCache() {
		logger.info("MAP cache is enabeld");
		return new LocalCache();
	}

	@Configuration
	@ConditionalOnProperty(value = "cache.type", havingValue = "redis", matchIfMissing = false)
	public class RedisCacheConfig {

		@Bean
		public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
			StringRedisTemplate template = new StringRedisTemplate();
			template.setConnectionFactory(factory);
			template.afterPropertiesSet();
			return template;
		}

		@Bean
		public Cache redisCache() {
			logger.info("REDIS cache is enabeld");
			return new RedisCache();
		}
	}

}
