package in.hashconnect.api.dao;

import static in.hashconnect.util.StringUtil.concate;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getIntValue;
import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.util.JsonUtil;

@SuppressWarnings("unchecked")
public class ApiDaoImpl implements ApiDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private Map<String, Map<String, Object>> apis;

	@Value("${common.controller.source:db}")
	private String source;

	@Override
	public Map<String, Object> getConfig(String name) {
		try {
			if ("file".equals(source)) {
				Map<String, Object> api = apis.get(name);

				if (MapUtils.isNotEmpty(api)) {
					return api;
				}
			}

			String query = "select config from masters_config where name=? limit 1";
			return JsonUtil.readValue(jdbcTemplate.queryForObject(query, String.class, name), Map.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void update(Map<String, Object> dbInsert, Map<String, Object> params) {
		String query = getString(dbInsert, "query");
		SqlParameterSource source = new CustomMapSqlParameterSource(params);
		namedParameterJdbcTemplate.update(query, source);
	}

	@Override
	public long create(Map<String, Object> dbInsert, Map<String, Object> params) {
		String query = getString(dbInsert, "query");
		SqlParameterSource source = new CustomMapSqlParameterSource(params);

		KeyHolder kh = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(query, source, kh);
		return kh.getKey().longValue();
	}

	@Override
	public GetResponse get(Map<String, Object> dbVariant, Map<String, Object> params) {
		String countQuery = getString(dbVariant, "countQuery");
		String query = getString(dbVariant, "query");
		boolean countQueryValid = isValid(countQuery);

		if (dbVariant.containsKey("customClause")) {
			String customClause = getString(dbVariant, "customClause");
			if (isValid(customClause)) {
				query = concate(query, " where ", customClause);
				if (countQueryValid)
					countQuery = concate(countQuery, " where ", customClause);
			}
		}
		
		String groupBy = getString(dbVariant, "groupBy");
		if (isValid(groupBy)) {
			query = concate(query, " group by  ", groupBy);
		}

		String orderBy = getString(dbVariant, "orderBy");
		if (isValid(orderBy)) {
			query = concate(query, " order by  ", orderBy);
		}
		
		if (params.containsKey("start") && params.containsKey("size")) {
			params.put("start", getIntValue(params, "start"));
			params.put("size", getIntValue(params, "size"));
			query = StringUtils.join(query, " limit :start,:size");
		}

		if (params.containsKey("search")) {
			params.put("search", StringUtils.join("%", getString(params, "search"), "%"));
		}

		GetResponse response = new GetResponse();

		if (countQueryValid)
			response.setTotalRecords(namedParameterJdbcTemplate.queryForObject(countQuery, params, Integer.class));
		response.setRecords(namedParameterJdbcTemplate.queryForList(query, params));
		return response;
	}

}
