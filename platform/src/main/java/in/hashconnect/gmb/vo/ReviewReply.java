package in.hashconnect.gmb.vo;

public class ReviewReply {

	private String comment;
	private String updateTime;
	private GoogleError error;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public GoogleError getError() {
		return error;
	}
	public void setError(GoogleError error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ReplyReviewResponse [comment=" + comment + ", updateTime="
				+ updateTime + ", error=" + error + "]";
	}
}
