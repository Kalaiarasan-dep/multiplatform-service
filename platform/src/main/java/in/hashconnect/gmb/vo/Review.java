package in.hashconnect.gmb.vo;

public class Review {

	private String reviewId;
	private Reviewer reviewer;
	private String starRating;
	private String comment;
	private String createTime;
	private String updateTime;
	private ReviewReply reviewReply;

	public String getReviewId() {
		return reviewId;
	}

	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	}

	public Reviewer getReviewer() {
		return reviewer;
	}

	public void setReviewer(Reviewer reviewer) {
		this.reviewer = reviewer;
	}

	public String getStarRating() {
		return starRating;
	}

	public void setStarRating(String starRating) {
		this.starRating = starRating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public ReviewReply getReviewReply() {
		return reviewReply;
	}

	public void setReviewReply(ReviewReply reviewReply) {
		this.reviewReply = reviewReply;
	}
}
