package in.hashconnect.notification.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.StringUtil;

public class NotificationDaoImpl extends JdbcDaoSupport implements NotificationDao {

	private String getTemplateByKey = "select subject,efrom,from_display_name,route,sender_id,dlt_template_id,dlt_entity_id from templates WHERE name=?";

	private String insertNotification = "insert into notifications (`template`,`type`,`to`,efrom,cc,params,subject,created_dt,client_id)values(?,?,?,?,?,?,?,now(),?)";

	private String updateNotificationById = "update notifications set status = ?, modified_dt = now(), api_response=?, reference_id=? where id=?";

	@Override
	public Map<String, Object> getTemplateDetailsByKey(String key) {
		try {
			return getJdbcTemplate().queryForMap(getTemplateByKey, key);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Long insertNotification(String template, String type, String to, String from, String cc, String subject,
			Map<String, Object> params) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new NotificationPSCreator(insertNotification, template, type, to, from, cc,
				JsonUtil.toString(params), subject), keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void updateNotification(Long id, String status, String apiResponse, String refId) {
		getJdbcTemplate().update(updateNotificationById, status, apiResponse, refId, id);
	}

	@Override
	public List<Map<String, Object>> getPendingNotification() {
		return getJdbcTemplate().queryForList("select id,reference_id,template from notifications where "
				+ "reference_id IS NOT NULL AND operator_status is null "
				+ "and created_dt<date_sub(now(), interval 15 minute) and (last_attempt_datetime is null or "
				+ "last_attempt_datetime <date_sub(now(), interval 15 minute)) and attempt < 3 limit 10");
	}

	@Override
	public void updateOperatorStatus(Long id, String status) {
		String query = "update notifications set operator_status=? where id=?";
		getJdbcTemplate().update(query, StringUtil.cut(status, 2000), id);

	}
}
