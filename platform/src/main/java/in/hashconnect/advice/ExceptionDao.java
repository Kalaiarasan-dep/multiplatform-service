package in.hashconnect.advice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import in.hashconnect.util.JsonUtil;

@Repository
public class ExceptionDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Value("${spring.application.name}")
	private String app;

	private static final String QUERY = "insert into app_errors(error,created_date,published,`type`)values(?,now(),'P',?)";

	public void log(Map<String, Object> params) {
		String error = JsonUtil.toString(params);
		jdbcTemplate.update(QUERY, error, app);
	}
}
