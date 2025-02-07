package in.hashconnect.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisCache implements Cache<String, String> {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Override
	public void put(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public void clear(String key) {
		redisTemplate.delete(key);
	}
}
