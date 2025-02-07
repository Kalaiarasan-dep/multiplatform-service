package in.hashconnect.gmb.vo;

public class LocationState {
	private boolean isGoogleUpdated;
	private boolean duplicate;
	private boolean suspended;
	private boolean canUpdate;
	private boolean canDelete;
	private boolean verified;
	private boolean needsReverification;
	private boolean pendingReview;
	private boolean disabled;
	private boolean published;
	private boolean disconnected;
	private boolean isLocalPostApiDisabled;
	private boolean hasPendingVerification;

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public boolean isCanUpdate() {
		return canUpdate;
	}

	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean isNeedsReverification() {
		return needsReverification;
	}

	public void setNeedsReverification(boolean needsReverification) {
		this.needsReverification = needsReverification;
	}

	public boolean isPendingReview() {
		return pendingReview;
	}

	public void setPendingReview(boolean pendingReview) {
		this.pendingReview = pendingReview;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}

	public boolean isHasPendingVerification() {
		return hasPendingVerification;
	}

	public void setHasPendingVerification(boolean hasPendingVerification) {
		this.hasPendingVerification = hasPendingVerification;
	}

	public boolean isGoogleUpdated() {
		return isGoogleUpdated;
	}

	public void setGoogleUpdated(boolean isGoogleUpdated) {
		this.isGoogleUpdated = isGoogleUpdated;
	}

	public boolean isLocalPostApiDisabled() {
		return isLocalPostApiDisabled;
	}

	public void setLocalPostApiDisabled(boolean isLocalPostApiDisabled) {
		this.isLocalPostApiDisabled = isLocalPostApiDisabled;
	}
}
