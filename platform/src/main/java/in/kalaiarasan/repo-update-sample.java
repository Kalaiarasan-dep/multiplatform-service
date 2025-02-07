
@Repository
public class GenericDaoImpl extends AbstractDao implements GenericDao {

	private static final Logger logger = LoggerFactory.getLogger(GenericDaoImpl.class);
	@Override
	public boolean validateUserByEmailOrMobile(String un, boolean mobile) {
		if (mobile)
			return getJdbcTemplate().queryForObject(
					"select count(uuid) from tenant_management.tm_user where mobile=? and status='Active' limit 1", Integer.class, un) > 0;

		return getJdbcTemplate().queryForObject(
				"select count(uuid) from tenant_management.tm_user where email=? and status='Active' limit 1", Integer.class, un) > 0;
	}


	@Override
	public String getUserId(String userName) {
		try {
			String sql = "SELECT bin_to_uuid(uuid) FROM tenant_management.tm_user WHERE (mobile = ? OR email=?)";
			return getJdbcTemplate().queryForObject(sql, new Object[]{userName,userName}, String.class);
		} catch (DataAccessException e) {
			logger.error("GenericDaoImpl | getUserId ", e);
			return null;
		}
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
