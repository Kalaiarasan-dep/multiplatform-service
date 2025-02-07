package in.hashconnect.api.dao;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.lang.Nullable;

public class CustomMapSqlParameterSource extends MapSqlParameterSource {
	public CustomMapSqlParameterSource(Map<String, ?> values) {
		super(values);
	}

	@Nullable
	public Object getValue(String paramName) {
		return getValues().get(paramName);
	}
}
