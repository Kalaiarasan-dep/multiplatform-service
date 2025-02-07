package in.hashconnect.dao;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import in.hashconnect.util.JsonUtil;

public class LoadTmpOrderConsumer implements Consumer<List<Object[]>> {
	private Integer refId;
	private JdbcTemplate jdbcTemplate;
	private String query;

	public LoadTmpOrderConsumer(Integer refId, JdbcTemplate jdbcTemplate, String query) {
		this.refId = refId;
		this.jdbcTemplate = jdbcTemplate;
		this.query = query;
	}

	@Override
	public void accept(List<Object[]> list) {
		try {
			jdbcTemplate.batchUpdate(query, list);
		} catch (Exception e) {
			String error = ExceptionUtils.getStackTrace(e);
			jdbcTemplate.update("insert into order_upload_failure(file_id,data,error,created_date)values(?,?,?,now())",
					refId, JsonUtil.toString(list), error);
		}
	}
}
