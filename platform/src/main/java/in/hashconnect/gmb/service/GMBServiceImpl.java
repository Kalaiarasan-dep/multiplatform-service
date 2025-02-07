package in.hashconnect.gmb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.gmb.dao.GMBDao;
import in.hashconnect.gmb.vo.AccountsResponse;
import in.hashconnect.gmb.vo.GoogleApiDetails;
import in.hashconnect.gmb.vo.GoogleError;
import in.hashconnect.gmb.vo.LocalPost;
import in.hashconnect.gmb.vo.LocationsResponse;
import in.hashconnect.gmb.vo.MediaItem;
import in.hashconnect.gmb.vo.MediaResponse;
import in.hashconnect.gmb.vo.Review;
import in.hashconnect.gmb.vo.ReviewReply;
import in.hashconnect.gmb.vo.ReviewResponse;
import in.hashconnect.http.client.HttpClient;
import in.hashconnect.http.client.HttpClientFactory;
import in.hashconnect.http.client.HttpClientFactory.TYPE;
import in.hashconnect.http.client.exception.UnAuthorizedException;
import in.hashconnect.util.JsonUtil;

public class GMBServiceImpl implements GMBService {
	private static final Logger logger = LoggerFactory.getLogger(GMBServiceImpl.class);

	private GMBDao gmbDao;

	public GMBServiceImpl(GMBDao gmbDao) {
		this.gmbDao = gmbDao;
	}

	@Override
	public AccountsResponse getAccounts() {
		logger.debug("getAccounts started");

		HttpClient httpClient = HttpClientFactory.get(TYPE.sun);

		String response = null;
		boolean retry = false;
		do {
			retry = false;
			try {
				GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("accounts");

				response = httpClient.doGet(apiDetails.getUrl(), prepareHeader(apiDetails));
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry);

		if (response == null) {
			logger.warn("getAccounts response is null");
			return null;
		}

		return JsonUtil.readValue(response, AccountsResponse.class);
	}

	@Override
	public LocationsResponse getLocationDetails(String accountId) {
		logger.debug("getLocationDetails started. - accountId: " + accountId);

		GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("locations");

		Map<String, String> map = prepareHeader(apiDetails);
		logger.debug("hitting " + apiDetails);

		HttpClient httpClient = HttpClientFactory.get(TYPE.sun);

		String url = String.format(apiDetails.getUrl(), accountId);

		String response = null;
		boolean retry = false;
		do {
			try {
				response = httpClient.doGet(url, map);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry);

		if (response == null) {
			logger.warn("getLocationDetails response is null");
			return null;
		}

		LocationsResponse locResponse = JsonUtil.readValue(response, LocationsResponse.class);

		while (locResponse.getNextPageToken() != null) {
			String nextPageUrl = url + "&pageToken=" + locResponse.getNextPageToken();

			response = httpClient.doGet(nextPageUrl, map);
			LocationsResponse tmpResposne = JsonUtil.readValue(response, LocationsResponse.class);

			if (tmpResposne == null)
				break;

			locResponse.getLocations().addAll(tmpResposne.getLocations());
			locResponse.setNextPageToken(tmpResposne.getNextPageToken());
		}

		return locResponse;
	}

	@Override
	public ReviewResponse getReviews(String locationId) {
		logger.debug("getReviews started. - locationId: " + locationId);

		HttpClient httpClient = HttpClientFactory.get(TYPE.sun);

		Map<String, String> headers = null;
		ReviewResponse response = null;
		String url = null;
		boolean retry = false;
		do {
			retry = false;
			try {
				GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("reviews");
				url = String.format(apiDetails.getUrl(), locationId);

				headers = prepareHeader(apiDetails);

				String strResponse = httpClient.doGet(url, headers);

				response = JsonUtil.readValue(strResponse, ReviewResponse.class);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry);

		if (response == null) {
			logger.warn("getReviews response is null");
			return null;
		}

		// see number of total records and run those many loops
		while (response.getNextPageToken() != null) {
			String nextPageUrl = url + "?pageToken=" + response.getNextPageToken();

			String strResponse = httpClient.doGet(nextPageUrl, headers);
			ReviewResponse tmpResponse = null;
			try {
				tmpResponse = JsonUtil.readValue(strResponse, ReviewResponse.class);
			} catch (Exception e) {
			}

			if (tmpResponse == null)
				break;

			List<Review> tmpReviews = tmpResponse.getReviews();
			if (tmpReviews != null && !tmpReviews.isEmpty()) {
				response.getReviews().addAll(tmpResponse.getReviews());
			}

			response.setNextPageToken(tmpResponse.getNextPageToken());
		}

		return response;
	}

	@Override
	public MediaResponse getMedia(String locationId) {
		logger.debug("getMedia started. - locationId: " + locationId);

		HttpClient httpClient = HttpClientFactory.get(TYPE.sun);

		MediaResponse response = null;
		String url = null;
		Map<String, String> headers = null;
		boolean retry = false;
		do {
			retry = false;
			try {
				GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("media");

				headers = prepareHeader(apiDetails);

				url = String.format(apiDetails.getUrl(), locationId);
				logger.debug("getMedia formattedUrl {}", url);

				String strResponse = httpClient.doGet(url, headers);

				response = JsonUtil.readValue(strResponse, MediaResponse.class);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry);

		if (response == null) {
			logger.warn("getMedia response is null");
			return null;
		}

		// see number of total records and run those many loops
		while (response.getNextPageToken() != null) {
			String nextPageUrl = url + "?pageToken=" + response.getNextPageToken();

			String strResponse = httpClient.doGet(nextPageUrl, headers);
			MediaResponse tmpResponse = null;
			try {
				tmpResponse = JsonUtil.readValue(strResponse, MediaResponse.class);
			} catch (Exception e) {
			}

			if (tmpResponse == null)
				break;

			List<MediaItem> tmpReviews = tmpResponse.getMediaItems();
			if (tmpReviews != null && !tmpReviews.isEmpty()) {
				response.getMediaItems().addAll(tmpResponse.getMediaItems());
			}

			response.setNextPageToken(tmpResponse.getNextPageToken());
		}

		return response;
	}

	@Override
	public MediaResponse getCustomerMedia(String locationId) {
		logger.debug("getCustomerMedia started. - locationId: " + locationId);

		HttpClient httpClient = HttpClientFactory.get(TYPE.sun);

		GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("customer-media");

		Map<String, String> map = prepareHeader(apiDetails);

		String url = String.format(apiDetails.getUrl(), locationId);

		MediaResponse response = null;
		boolean retry = false;
		do {
			try {
				retry = false;
				String strResponse = httpClient.doGet(url, map);

				response = JsonUtil.readValue(strResponse, MediaResponse.class);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry);

		if (response == null) {
			logger.warn("getCustomerMedia response is null");
			return null;
		}

		// see number of total records and run those many loops
		while (response.getNextPageToken() != null) {
			String nextPageUrl = url + "?pageToken=" + response.getNextPageToken();

			String strResponse = httpClient.doGet(nextPageUrl, map);
			MediaResponse tmpResponse = null;
			try {
				tmpResponse = JsonUtil.readValue(strResponse, MediaResponse.class);
			} catch (Exception e) {
			}

			if (tmpResponse == null)
				break;

			List<MediaItem> tmpReviews = tmpResponse.getMediaItems();
			if (tmpReviews != null && !tmpReviews.isEmpty()) {
				response.getMediaItems().addAll(tmpResponse.getMediaItems());
			}

			response.setNextPageToken(tmpResponse.getNextPageToken());
		}

		return response;
	}

	@Override
	public ReviewReply replyToReview(String reviewId, String message) {
		ReviewReply response = null;
		boolean retry = false;

		HttpClient httpClient = HttpClientFactory.get(TYPE.apache);

		byte maxRetry = 3, retryIdx = 0;
		do {
			retry = false;
			GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("reply");

			String url = String.format(apiDetails.getUrl(), reviewId);

			Map<String, String> headers = prepareHeader(apiDetails);

			String request = String.format("{comment: \"%s\"}", message);
			try {
				String strResponse = httpClient.doPut(url, request, headers);
				response = JsonUtil.readValue(strResponse, ReviewReply.class);

				retry = isUnAuthorized(response);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			}
		} while (retry && retryIdx++ < maxRetry);

		return response;
	}

	private boolean isUnAuthorized(ReviewReply response) {
		return response != null ? isUnAuthorized(response.getError()) : false;
	}

	private boolean isUnAuthorized(GoogleError error) {
		if (error != null && error.getCode() != null)
			return error.getCode() == 401;
		return false;
	}

	private boolean isUnthorized(LocalPost response) {
		return response != null ? isUnAuthorized(response.getError()) : false;
	}

	@Override
	public LocalPost createPost(String locationId, LocalPost localPost) {
		logger.debug("createPost started. - locationId: " + locationId);
		HttpClient httpClient = HttpClientFactory.get(TYPE.apache);

		boolean retry = false;
		byte maxRetry = 3, retryIdx = 0;
		LocalPost response = null;
		do {
			GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("localpost");

			Map<String, String> map = prepareHeader(apiDetails);

			String url = String.format(apiDetails.getUrl(), locationId);

			retry = false;
			try {
				String req = JsonUtil.toString(localPost);

				String strResponse = httpClient.doPost(url, req, map);
				logger.info("response from createPost {}", strResponse);
				response = JsonUtil.readValue(strResponse, LocalPost.class);
				response.setStatus("SUCCESS");

				retry = isUnthorized(response);
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			} catch (Exception e) {
				logger.error("failed to post. localtionId: " + locationId, e);
				response.setStatus("FAILED");
			}
		} while (retry && retryIdx++ < maxRetry);
		return response;
	}

	@Override
	public LocalPost updatePost(String postId, String key, LocalPost localPost) {
		logger.debug("updatePost started. - postId: " + postId);
		HttpClient httpClient = HttpClientFactory.get(TYPE.apache);

		boolean retry = false;
		LocalPost response = null;
		do {
			GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("updatepost");

			Map<String, String> map = prepareHeader(apiDetails);

			String url = String.format(apiDetails.getUrl(), postId) + "?updateMask=" + key;

			retry = false;
			try {
				String strResponse = httpClient.doPatch(url, JsonUtil.toString(localPost), map);
				logger.info("response from updatePost {}", strResponse);
				response = JsonUtil.readValue(strResponse, LocalPost.class);
				response.setStatus("SUCCESS");
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			} catch (Exception e) {
				logger.error("failed to updatePost. postId: " + postId, e);
				response.setStatus("FAILED");
			}
		} while (retry);
		return response;
	}

	@Override
	public String deletePost(String postId) {
		logger.debug("deletePost started. - postId: " + postId);
		HttpClient httpClient = HttpClientFactory.get(TYPE.apache);

		String status = null;
		boolean retry = false;
		do {
			GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("deletepost");

			Map<String, String> map = prepareHeader(apiDetails);

			String url = String.format(apiDetails.getUrl(), postId);

			retry = false;
			try {
				httpClient.doDelete(url, map);
				status = "SUCCESS";
				return status;
			} catch (UnAuthorizedException e) {
				refreshToken();
				retry = true;
			} catch (Exception e) {
				logger.error("deletePost failed. postId: " + postId, e);
			}
		} while (retry);
		return status;
	}

	private Map<String, String> prepareHeader(GoogleApiDetails apiDetails) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Authorization", apiDetails.getAccessToken());
		return map;
	}

	public synchronized void refreshToken() {
		GoogleApiDetails apiDetails = gmbDao.getGoogleApiDetails("refresh_token");

		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/x-www-form-urlencoded");

		String url = apiDetails.getUrl();
		Map<String, String> params = new HashMap<>();
		params.put("refresh_token", apiDetails.getRefreshToken());
		params.put("client_id", apiDetails.getClientId());
		params.put("client_secret", apiDetails.getClientSecret());
		params.put("grant_type", "refresh_token");

		String response = HttpClientFactory.get(TYPE.apache).doPost(url, params, headers);

		@SuppressWarnings("unchecked")
		Map<String, String> tokenResponse = JsonUtil.readValue(response, Map.class);

		if (tokenResponse == null)
			return;

		String accessToken = tokenResponse.get("access_token");
		String tokenType = tokenResponse.get("token_type");

		gmbDao.updateAccessToken(accessToken, tokenType);
	}

}
