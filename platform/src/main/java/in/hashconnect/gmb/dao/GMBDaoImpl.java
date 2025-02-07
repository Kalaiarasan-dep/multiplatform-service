package in.hashconnect.gmb.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import in.hashconnect.gmb.vo.GoogleApiDetails;

public class GMBDaoImpl extends JdbcDaoSupport implements GMBDao {

	public GoogleApiDetails getGoogleApiDetails(String type) {
		String query = "select url,concat(token_type,' ',access_token) accessToken, client_id clientId, "
				+ "client_secret clientSecret, refresh_token refreshToken from google_client_details where type=?";
		return getJdbcTemplate().queryForObject(query,
				new BeanPropertyRowMapper<GoogleApiDetails>(GoogleApiDetails.class), type);
	}

	@Override
	public void updateAccessToken(String accessToken, String tokenType) {
		String query = "update google_client_details set access_token=?, token_type=?";
		getJdbcTemplate().update(query, accessToken, tokenType);
	}
}
