package in.hashconnect.dao.impl;

import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.common.vo.KarixStatus;
import in.hashconnect.controller.vo.OtpEntry;
import in.hashconnect.dao.GenericDao;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;


@Repository
@PropertySource({ "classpath:queries.properties" })
public class GenericDaoImpl extends JdbcDaoSupport implements GenericDao {

	public GenericDaoImpl(@Autowired JdbcTemplate jdbcTemplate) {
		setJdbcTemplate(jdbcTemplate);
	}

	@Value("${get.template.by.key}")
	private String getTemplateByKey;

	@Value("${insert.notification}")
	private String insertNotification;

	@Value("${update.notification.by.id}")
	private String updateNotificationById;

	@Override
	public Map<String, Object> getTemplateDetailsByKey(String key) {
		try {
			return getJdbcTemplate().queryForMap(getTemplateByKey, key);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	public String getTemplateDetailsByRefId(String refid) {
		try {
			String sql="select template from notifications where reference_id =?";
			return getJdbcTemplate().queryForObject(sql,String.class,refid);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Long insertNotification(Map<String, Object> notificationParams, Map<String, Object> params, Long clientId) {
		Integer template = MapUtils.getInteger(notificationParams,"template");
		String to = MapUtils.getString(notificationParams,"to");
		String from = MapUtils.getString(notificationParams,"from");
		String cc = MapUtils.getString(notificationParams,"cc");
		String subject = MapUtils.getString(notificationParams,"subject");

		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new NotificationPSCreator(insertNotification, template, to, from, cc,
				JsonUtil.toString(params), subject, clientId), keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void updateNotification(Long id, String status, String apiResponse,String refId) {
		getJdbcTemplate().update(updateNotificationById, status, apiResponse, id);
	}

	@Override
	public List<KarixStatus> getOpearatorStsPendingRecords(String str){
		String sql =
				"SELECT  reference_id, template " +
						"FROM notifications " +
						"WHERE operator_status IS NULL " +
						"AND reference_id IS NOT NULL " +
						"AND (DATE_ADD(IFNULL(modified_dt, created_dt), " +
						"    INTERVAL (SELECT JSON_EXTRACT(t.value, CONCAT('$.\\\"', jt.key, '\\\"')) AS value " +
						"              FROM settings t " +
						"              CROSS JOIN JSON_TABLE(JSON_KEYS(t.value), '$[*]' COLUMNS (`key` VARCHAR(10) PATH '$')) AS jt " +
						"              WHERE t.name = 'karix_retry_attempt' AND jt.key = IFNULL(attempt,0) + 1) MINUTE) < NOW()) " +
						"AND IFNULL(attempt,0) < ?" +
						"LIMIT 100";

		return getJdbcTemplate().query(sql, new Object[] {str},
				new BeanPropertyRowMapper<KarixStatus>(KarixStatus.class));
	}

	@Override
	public void updateNotificationStatus(String refId, String operatorSts) {
		String query = "update notifications set operator_status=?, attempt=attempt+1 where reference_id=? ";
		getJdbcTemplate().update(query,operatorSts,refId);
	}

	@Override
	public Long addOtp(OtpEntry otpEntry){
		try {
			String sql = "INSERT INTO `tenant_management`.`tm_otp` ( `sent_to`, `otp`, `is_validated`, `sent_at`, `expires_at`, `purpose`, `requesting_ip_address`, `tm_user_uuid`) " +
					"VALUES (?, ?, ?, NOW(), (DATE_ADD(NOW(), INTERVAL ? MINUTE)), ?, ?, (SELECT uuid FROM tenant_management.tm_user WHERE (mobile = ? OR email = ?)))";

			KeyHolder keyHolder = new GeneratedKeyHolder();

			getJdbcTemplate().update(
					connection -> {
						PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						ps.setString(1, otpEntry.getSentTo());
						ps.setString(2, otpEntry.getOtp());
						ps.setInt(3, otpEntry.getIsValidated());
						ps.setLong(4, otpEntry.getExpiryInterval());
						ps.setString(5, otpEntry.getPurpose());
						ps.setString(6, otpEntry.getIp());
						ps.setString(7, otpEntry.getSentTo());
						ps.setString(8, otpEntry.getSentTo());
						return ps;
					},
					keyHolder
			);

			long key = keyHolder.getKey().longValue();
			return key;

		} catch (DataAccessException e) {
			logger.error("GenericDaoImpl || addOtp ", e);
			return null;
		}
	}


	@Override
	public int getOtpByIntervalAndIp(String ip, Integer interval){
		try {
			String sql = "select count(*) from tenant_management.tm_otp  where requesting_ip_address=? and sent_at > DATE_SUB(NOW(), INTERVAL ? MINUTE)";
			return getJdbcTemplate().queryForObject(sql, new Object[]{ip, interval}, Integer.class);
		}catch (DataAccessException e){
			return 0;
		}
	}

	@Override
	public Boolean validateOtp(String refId,String otp) {
		try {
			String sql = "select id from tenant_management.tm_otp where otp=? and id=? and expires_at > NOW() and is_validated=0  order by id desc limit 10";
			Long id = getJdbcTemplate().queryForObject(sql, new Object[]{otp, refId}, Long.class);
			if (id != null) {
				if(updateAsUsed(id))
					return true;
				else
					return false;
			}
			return false;
		} catch (DataAccessException e) {
			logger.error("GenericDao || validateOtp ",e);
			return false;
		}

	}
	private Boolean updateAsUsed(Long id){
		try{
			String sql="update tenant_management.tm_otp  set is_validated=1 where id=?";
			getJdbcTemplate().update(sql,id);
			return true;
		}catch (DataAccessException e){
			logger.error("GenericDao || updateAsUsed ",e);
			return false;
		}


	}
}
