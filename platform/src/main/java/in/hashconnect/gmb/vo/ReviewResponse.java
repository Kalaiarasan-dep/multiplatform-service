package in.hashconnect.gmb.vo;

import java.util.List;

public class ReviewResponse {
	private List<Review> reviews;
	private String averageRating;
	private Integer totalReviewCount;
	private String nextPageToken;

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getTotalReviewCount() {
		return totalReviewCount;
	}

	public void setTotalReviewCount(Integer totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}
}
