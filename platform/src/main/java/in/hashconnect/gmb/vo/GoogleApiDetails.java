package in.hashconnect.gmb.vo;

public class GoogleApiDetails {

	private String url;
	private String accessToken;
	private String clientId;
	private String clientSecret;
	private String refreshToken;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return "GoogleApiDetails [url=" + url + ", accessToken=" + accessToken
				+ ", clientId=" + clientId + ", clientSecret=" + clientSecret
				+ ", refreshToken=" + refreshToken + "]";
	}

}
