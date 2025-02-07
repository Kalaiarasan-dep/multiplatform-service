package in.hashconnect.gmb.vo;

import java.util.List;

public class LocalPostResponse {
	private String nextPageToken;
	private List<LocalPost> localPosts;
	private GoogleError error;
	
	public String getNextPageToken() {
		return nextPageToken;
	}
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
	public List<LocalPost> getLocalPosts() {
		return localPosts;
	}
	public void setLocalPosts(List<LocalPost> localPosts) {
		this.localPosts = localPosts;
	}
	public GoogleError getError() {
		return error;
	}
	public void setError(GoogleError error) {
		this.error = error;
	}

}
