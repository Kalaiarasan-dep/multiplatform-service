package in.hashconnect.gmb.service;

import in.hashconnect.gmb.vo.AccountsResponse;
import in.hashconnect.gmb.vo.LocalPost;
import in.hashconnect.gmb.vo.LocationsResponse;
import in.hashconnect.gmb.vo.MediaResponse;
import in.hashconnect.gmb.vo.ReviewReply;
import in.hashconnect.gmb.vo.ReviewResponse;

public interface GMBService {
	AccountsResponse getAccounts();

	LocationsResponse getLocationDetails(String accountId);

	ReviewResponse getReviews(String locationId);

	MediaResponse getMedia(String locationId);

	MediaResponse getCustomerMedia(String locationId);

	ReviewReply replyToReview(String reviewId, String message);

	LocalPost createPost(String locationId, LocalPost localPost);

	LocalPost updatePost(String postId, String key, LocalPost localPost);

	String deletePost(String postId);
}
