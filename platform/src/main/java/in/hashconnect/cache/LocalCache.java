package in.hashconnect.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class LocalCache implements Cache<String, String> {
	private final Map<String, String> map = new HashMap<>();

	@Override
	public void put(String key, String value) {
		map.put(key, value);
	}

	@Override
	public String get(String key) {
		return map.get(key);
	}

	@Override
	public void clear(String key) {
		map.remove(key);
	}
}
