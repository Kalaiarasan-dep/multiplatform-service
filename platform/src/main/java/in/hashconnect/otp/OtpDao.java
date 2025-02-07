package in.hashconnect.otp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import in.hashconnect.otp.vo.Request;
import in.hashconnect.util.AESUtil;

public class OtpDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AESUtil aesUtil;

	public String save(Request request) {
		request.to(aesUtil.encrypt(request.getTo()));

		String query = "insert into user_otp (otp,status,originating_ip_address,mobile_number,expiration_timestamp, creation_timestamp) values(?,?,?,?,?,now())";

		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				int idx = 1;
				ps.setString(idx++, request.getOtp());
				ps.setString(idx++, "NEW");
				ps.setString(idx++, request.getIp());
				ps.setString(idx++, request.getTo());
				ps.setTimestamp(idx++, request.getExpiryTime());
				return ps;
			}
		};

		jdbcTemplate.update(psc, keyHolder);
		long key = keyHolder.getKey().longValue();

		return aesUtil.encrypt(key + ":" + request.getTo());
	}

	public Map<String, Object> validate(Request request) {
		String refId = aesUtil.decrypt(request.getRefId());

		String[] values = refId.split(":");
		long key = Long.valueOf(values[0]);
		String to = values[1];

		String query = "update user_otp set status='USED' where id=? and mobile_number=? and otp=? and status='NEW'";

		boolean valid = jdbcTemplate.update(query, key, to, request.getOtp()) > 0;

		Map<String, Object> result = new HashMap<>(2);
		result.put("valid", valid);
		result.put("to", aesUtil.decrypt(to));

		return result;
	}

	public Map<String, Object> getByRefId(Request request) {
		String refId = aesUtil.decrypt(request.getRefId());

		String[] values = refId.split(":");
		long key = Long.valueOf(values[0]);

		String query = "select otp from user_otp where id=? and status='NEW'";

		Map<String, Object> result = new HashMap<>(2);
		try {
			String otp = jdbcTemplate.queryForObject(query, String.class, key);

			result.put("otp", otp);
			result.put("to", aesUtil.decrypt(values[1]));

			return result;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean rateLimit(Request request) {
		String query = "select count(*) >= (select value from settings where name='otp_rate_limit') "
				+ "from user_otp where originating_ip_address=? and creation_timestamp >= date_sub(now(), interval "
				+ "(select value from settings where name='otp_rate_limit_interval') minute)";
		return jdbcTemplate.queryForObject(query, Integer.class, request.getIp()) == 0;
	}
}
