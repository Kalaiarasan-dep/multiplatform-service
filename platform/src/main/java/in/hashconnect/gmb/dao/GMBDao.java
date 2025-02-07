package in.hashconnect.gmb.dao;

import in.hashconnect.gmb.vo.GoogleApiDetails;

public interface GMBDao {
	public GoogleApiDetails getGoogleApiDetails(String type);

	public void updateAccessToken(String accessToken, String tokenType);
}
