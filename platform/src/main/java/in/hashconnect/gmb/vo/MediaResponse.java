package in.hashconnect.gmb.vo;

import java.util.List;

public class MediaResponse {
	private List<MediaItem> mediaItems;
	private Integer totalMediaItemCount;
	private String nextPageToken;

	public List<MediaItem> getMediaItems() {
		return mediaItems;
	}

	public void setMediaItems(List<MediaItem> mediaItems) {
		this.mediaItems = mediaItems;
	}

	public Integer getTotalMediaItemCount() {
		return totalMediaItemCount;
	}

	public void setTotalMediaItemCount(Integer totalMediaItemCount) {
		this.totalMediaItemCount = totalMediaItemCount;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
}
