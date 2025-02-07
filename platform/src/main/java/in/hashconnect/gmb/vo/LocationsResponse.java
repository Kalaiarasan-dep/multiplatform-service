package in.hashconnect.gmb.vo;

import java.util.List;

public class LocationsResponse {

	private String nextPageToken;
	private List<Location> locations;

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
}
