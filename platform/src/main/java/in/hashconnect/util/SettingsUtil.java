package in.hashconnect.util;

import static in.hashconnect.util.StringUtil.convertToInt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import in.hashconnect.cache.Cache;

public class SettingsUtil {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Cache<String, String> cache;

	public String getValue(String name) {
		String value = cache.get(name);
		if (StringUtil.isValid(value))
			return value;

		try {
			String query = "select value from settings where name=? limit 1";
			value = jdbcTemplate.queryForObject(query.toString(), String.class, name);

			cache.put(name, value);
		} catch (EmptyResultDataAccessException e) {
		}
		return value;
	}

	public Integer getIntValue(String name) {
		return convertToInt(getValue(name));
	}
}
