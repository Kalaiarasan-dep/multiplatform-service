package in.hashconnect.vo;

import java.util.Map;

public class TokenCreateRequest {

	private Map<String, Object> claims;
	private Integer validityInHours;

	public Map<String, Object> getClaims() {
		return claims;
	}

	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}

	public Integer getValidityInHours() {
		return validityInHours;
	}

	public void setValidityInHours(Integer validityInHours) {
		this.validityInHours = validityInHours;
	}
}
