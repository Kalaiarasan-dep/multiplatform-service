package in.hashconnect.zoho;

import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getMap;
import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.http.client.HttpClient;
import in.hashconnect.http.client.HttpClientFactory;
import in.hashconnect.http.client.HttpClientFactory.TYPE;
import in.hashconnect.http.client.exception.UnAuthorizedException;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.HttpUtil;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.zoho.dao.ZohoDao;
import in.hashconnect.zoho.exception.ZohoInvalidResponseException;
import in.hashconnect.zoho.vo.ApplyCreditNoteRequest;
import in.hashconnect.zoho.vo.ApproveInvoiceRequest;
import in.hashconnect.zoho.vo.BillingAddress;
import in.hashconnect.zoho.vo.ContactPerson;
import in.hashconnect.zoho.vo.CreateBillRequest;
import in.hashconnect.zoho.vo.CreateCustomerRequest;
import in.hashconnect.zoho.vo.CreateInvoiceRequest;
import in.hashconnect.zoho.vo.CreatePaymentRequest;
import in.hashconnect.zoho.vo.RefundPaymentRequest;
import in.hashconnect.zoho.vo.ZohoItem;

@SuppressWarnings("unchecked")
public class ZohoApiProcessorImpl implements ZohoApiProcessor {
	private static Logger logger = LoggerFactory.getLogger(ZohoApiProcessorImpl.class);

	@Autowired
	private ZohoDao zohoDao;

	private HttpClient sunHttpClient;

	private HttpClient apacheHttpClient;

	@PostConstruct
	public void init() {
		apacheHttpClient = HttpClientFactory.get(TYPE.apache);
		sunHttpClient = HttpClientFactory.get(TYPE.sun);
	}

	public void getItems() {
		logger.info("getItems()");
		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("items");

		// truncate table before restarting.
		zohoDao.truncateZohoItem();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-com-zoho-invoice-organizationid", getString(apiDetails, "organization_id"));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		int pageNo = 1;
		boolean hasMorePages = false;
		do {
			String url = getString(apiDetails, "url") + "?page=" + (pageNo++);
			Map<String, Object> response = null;

			int counter = 1, maxCouter = 3;
			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

				try {
					String apiResponse = sunHttpClient.doGet(url, headers);
					response = JsonUtil.readValue(apiResponse, Map.class);
				} catch (RuntimeException e) {
					if (e.getMessage().equals("Unauthorized") && counter++ <= maxCouter)
						unauthorized = true;
				}
			} while (unauthorized);

			if (!isValidResponse(response)) {
				throw new ZohoInvalidResponseException(response);
			}

			List<Map<String, Object>> items = (List<Map<String, Object>>) getObject(response, "items");
			Map<String, Object> pageContext = (Map<String, Object>) getMap(response, "page_context");

			zohoDao.saveZohoItems(items);

			hasMorePages = getBooleanValue(pageContext, "has_more_page");

		} while (hasMorePages);
	}

	public void downloadItemsDaily() {
		if (zohoDao.canDownloadReviews()) {
			getItems();
		}
	}

	public Map<String, Object> createInvoice(CreateInvoiceRequest invoice) {
		logger.info("createInvoice: {}", invoice);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("invoice");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(invoice);

		if (invoice.isInsertToDB())
			zohoDao.createInvoice(invoice, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String response = sunHttpClient.doPost(getString(apiDetails, "url"), request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "invoice");

				if (invoice.isInsertToDB())
					zohoDao.createInvoice(invoice, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> updateInvoice(CreateInvoiceRequest invoice) {
		logger.info("updateInvoice: {}", invoice);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-invoice");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(invoice);

		zohoDao.updateInvoice(invoice, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice-id", invoice.getInvoice_no());

				String response = apacheHttpClient.doPut(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "invoice");

				zohoDao.updateInvoice(invoice, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> approveInvoice(ApproveInvoiceRequest request) {
		logger.info("approveInvoice: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("approve-invoice");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice-id", request.getInvoiceId());

				logger.info("approveInvoice. url: {}, headers:{}", url, headers);

				String response = sunHttpClient.doPost(url, null, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;

			} catch (UnAuthorizedException e) {
				logger.error("approveInvoice: unauthorized {}", e.getMessage());
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> voidInvoice(ApproveInvoiceRequest request) {
		logger.info("voidInvoice: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("void-invoice");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice-id", request.getInvoiceId());

				logger.info("voidInvoice. url: {}, headers:{}", url, headers);

				String response = sunHttpClient.doPost(url, null, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;

			} catch (UnAuthorizedException e) {
				logger.error("voidInvoice: unauthorized {}", e.getMessage());
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> createPayment(CreatePaymentRequest payment) {
		logger.info("createPayment: {}", payment);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("create-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		zohoDao.createPayment(payment, null);

		String request = JsonUtil.toString(payment);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String response = sunHttpClient.doPost(getString(apiDetails, "url"), request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "payment");

				zohoDao.createPayment(payment, responseMap);
				responseMap.put("dbPayId", payment.getPayId());

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> createContact(CreateCustomerRequest request) {
		logger.info("createContact: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("contact");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String strRequest = JsonUtil.toString(request);

		zohoDao.createCustomer(request, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String response = sunHttpClient.doPost(getString(apiDetails, "url"), strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "contact");
				// save to zoho_customer table
				zohoDao.createCustomer(request, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public List<Map<String, Object>> getContact(String search) {
		logger.info("getContact: {}", search);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-contacts");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-com-zoho-invoice-organizationid", getString(apiDetails, "organization_id"));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		int pageNo = 1;
		boolean hasMorePages = false;

		List<Map<String, Object>> contacts = new ArrayList<Map<String, Object>>();

		int counter = 1, maxCouter = 3;
		do {
			String url = getString(apiDetails, "url") + "&search_text=" + search + "&page=" + (pageNo++);
			Map<String, Object> response = null;

			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

				try {
					String apiResponse = sunHttpClient.doGet(url, headers);
					response = JsonUtil.readValue(apiResponse, Map.class);

					unauthorized = false;
				} catch (RuntimeException e) {
					if (e.getMessage().equals("Unauthorized") && (counter++ <= maxCouter))
						unauthorized = true;
				}
			} while (unauthorized);

			if (!isValidResponse(response)) {
				throw new ZohoInvalidResponseException(response);
			}

			contacts.addAll((List<Map<String, Object>>) getObject(response, "contacts"));
			Map<String, Object> pageContext = (Map<String, Object>) getMap(response, "page_context");

			hasMorePages = getBooleanValue(pageContext, "has_more_page");

		} while (hasMorePages);

		return contacts;

	}

	private synchronized void refreshToken(Map<String, Object> apiDetails) {
		String clientId = getString(apiDetails, "client_id");
		String clientSecret = getString(apiDetails, "client_secret");
		String refreshToken = getString(apiDetails, "refresh_token");

		String accessTokenUrl = "https://accounts.zoho.com/oauth/v2/token";
		String body = "client_id=" + clientId + "&client_secret=" + clientSecret + "&refresh_token=" + refreshToken
				+ "&grant_type=refresh_token";

		String tokenRawResponse = sunHttpClient.doPost(accessTokenUrl, body);
		logger.info("refreshing token.response: {} ", tokenRawResponse);

		Map<String, Object> tokenResponse = JsonUtil.readValue(tokenRawResponse, Map.class);
		String accessToken = getString(tokenResponse, "access_token");
		apiDetails.put("access_token", accessToken);

		// update zoho accessToken
		zohoDao.updateZohoToken(accessToken);
	}

	public Map<String, Object> updatePayment(CreatePaymentRequest paymentData) {
		logger.info("updatePayment: {}", paymentData);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(paymentData);

		zohoDao.updatePayment(paymentData, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("payment-id", paymentData.getPayment_id());

				String response = apacheHttpClient.doPut(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "payment");

				zohoDao.updatePayment(paymentData, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> refundPayment(RefundPaymentRequest request) {
		logger.info("refundPayment: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("refund-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String url = getString(apiDetails, "url");
		url = url.replace("payment-id", request.getCustomer_payment_id());

		request.setRequestUrl(url);
		String strRequest = JsonUtil.toString(request);
		zohoDao.createRefund(request, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String response = sunHttpClient.doPost(url, strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "payment_refund");

				zohoDao.createRefund(request, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> listPayments(Map<String, String> request) {
		logger.info("listPayments: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-payment");

		Map<String, String> headers = new HashMap<String, String>();

		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String requestStr = request.keySet().stream().map(k -> {
					try {
						return k + "=" + URLEncoder.encode(request.get(k), "utf8");
					} catch (Exception e) {
					}
					return null;
				}).filter(v -> v != null).collect(Collectors.joining("&"));

				String url = getString(apiDetails, "url") + requestStr;

				String response = sunHttpClient.doGet(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;
			} catch (UnAuthorizedException e) {
				unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	private boolean isValidResponse(Map<String, Object> response) {
		try {
			return getInteger(response, "code") == 0;
		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, Object> updateContact(CreateCustomerRequest customerData) {
		logger.info("updateContact: {}", customerData);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-contact");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(customerData);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("customer-id", customerData.getCustomer_id());

				String response = apacheHttpClient.doPut(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "contact");

				zohoDao.updateCustomer(customerData, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> createCreditNote(CreateInvoiceRequest creditNote) {
		logger.info("createCreditNote: {}", creditNote);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("create-credit-note");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(creditNote);
		zohoDao.createCreditNote(creditNote, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url += creditNote.getInvoice_no();

				String response = sunHttpClient.doPost(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "creditnote");

				zohoDao.createCreditNote(creditNote, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> deleteInvoicePayment(Long invoiceId, String zohoInvoiceId, String zohoInvoicePaymentId) {
		logger.info("deleteInvoicePayment: invoiceId:{}, zohoInvoiceId:{}, zohoInvoicePaymentId:{}", invoiceId,
				zohoInvoiceId, zohoInvoicePaymentId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("delete-invoice-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		Long id = zohoDao.deletePaymentFromInvoice(null, invoiceId, zohoInvoiceId, zohoInvoicePaymentId, null);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice_id", zohoInvoiceId);
				url = url.replace("invoice_payment_id", zohoInvoicePaymentId);

				String response = sunHttpClient.doDelete(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				zohoDao.deletePaymentFromInvoice(id, invoiceId, zohoInvoiceId, zohoInvoicePaymentId, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> deleteCustomerPayment(Long paymentId, String customerPaymentId) {
		logger.info("deleteCustomerPayment: paymentId:{}, customerPaymentId:{}", paymentId, customerPaymentId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("delete-customer-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("customer_payment_id", customerPaymentId);

				String response = sunHttpClient.doDelete(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> updateInvoiceAddress(Long invoiceId, String zohoInvoiceId, String type,
			BillingAddress billingAddress) {
		logger.info("updateInvoiceAddress: invoiceId:{}, zohoInvoiceId:{}, type:{}, billingAddress:{}", invoiceId,
				zohoInvoiceId, type, billingAddress);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("invoice-update-address");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(billingAddress);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice_id", zohoInvoiceId);
				url = url.replace("type", type);

				String response = apacheHttpClient.doPut(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> deleteInvoice(Long invoiceId, String zohoInvoiceId) {
		logger.info("deleteInvoice: invoiceId:{}, zohoInvoiceId:{}", invoiceId, zohoInvoiceId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("delete-invoice");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice_id", zohoInvoiceId);

				String response = sunHttpClient.doDelete(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> getCustomer(Long customerId, Map<String, Object> queryParams) {
		logger.info("getCustomer: queryParams:{}", queryParams);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("get-customer");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url += HttpUtil.mapToQueryParams(queryParams);

				String response = sunHttpClient.doGet(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				List<Map<String, Object>> contacts = (List<Map<String, Object>>) getObject(responseMap, "contacts");
				if (contacts == null || contacts.isEmpty() || contacts.size() > 1) {
					throw new ZohoInvalidResponseException("no contact or more than 1 found");
				}

				responseMap = contacts.get(0);

				zohoDao.updateCustomer(customerId, responseMap);

				return responseMap;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return null;
	}

	@Override
	public Map<String, Object> createBill(CreateBillRequest billRequest) {
		logger.info("createBill: {}", billRequest);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("create-bill");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(billRequest);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");

				String response = sunHttpClient.doPost(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return (Map<String, Object>) getMap(responseMap, "bill");
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Object> updateBill(CreateBillRequest billRequest) {
		logger.info("updateBill: {}", billRequest);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-bill");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String request = JsonUtil.toString(billRequest);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("bill_id", billRequest.getBillId());

				String response = sunHttpClient.doPut(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return (Map<String, Object>) getMap(responseMap, "bill");
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public List<Map<String, Object>> listBills(Map<String, String> request) {
		logger.info("listBills: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-bill");

		List<Map<String, Object>> bills = new ArrayList<>();

		Map<String, String> headers = new HashMap<String, String>();

		int page = 1;
		boolean hasMorePages = false;
		do {
			Map<String, Object> responseMap = null;
			String response = null;

			int counter = 1, maxCouter = 3;
			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
				try {
					String requestStr = request.keySet().stream().map(k -> {
						try {
							return k + "=" + URLEncoder.encode(request.get(k), "utf8");
						} catch (Exception e) {
						}
						return null;
					}).filter(v -> v != null).collect(Collectors.joining("&"));

					String url = StringUtil.concate(getString(apiDetails, "url"), "page=", (page), "&", requestStr);

					response = sunHttpClient.doGet(url, headers);
					responseMap = JsonUtil.readValue(response, Map.class);

				} catch (UnAuthorizedException e) {
					if (counter++ <= maxCouter)
						unauthorized = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} while (unauthorized);

			if (!isValidResponse(responseMap)) {
				throw new ZohoInvalidResponseException(response);
			}

			List<Map<String, Object>> tmpBills = (List<Map<String, Object>>) getObject(responseMap, "bills");
			tmpBills.stream().forEach(map -> {
				String billId = getString(map, "bill_id");

				Map<String, Object> bill = getBill(billId);
				if (bill != null)
					bills.add(bill);
			});

			Map<String, Object> pageContext = (Map<String, Object>) getMap(responseMap, "page_context");
			hasMorePages = getBooleanValue(pageContext, "has_more_page");

			page++;
		} while (hasMorePages);

		return bills;
	}

	public Map<String, Object> getBill(String billId) {
		logger.info("getBill: {}", billId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("get-bill");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
		headers.put("Accept", "application/json");

		String url = getString(apiDetails, "url");
		url = url.replace("bill_id", billId);

		Map<String, Object> responseMap = null;

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;
			try {
				String response = sunHttpClient.doGet(url, headers);
				responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		if (isValidResponse(responseMap)) {
			return (Map<String, Object>) getMap(responseMap, "bill");
		}
		return null;
	}

	@Override
	public Map<String, Object> updateContactPerson(ContactPerson request) {
		logger.info("updateContactPerson: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-contact-person");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String strRequest = JsonUtil.toString(request);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("contact_person_id", request.getContact_person_id());

				String response = apacheHttpClient.doPut(url, strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return (Map<String, Object>) getMap(responseMap, "contact_person");
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public List<Map<String, Object>> listVendorPaymets(Map<String, String> request) {
		logger.info("listVendorPaymets: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-vendor-payments");

		List<Map<String, Object>> vendorPayments = new ArrayList<>();

		Map<String, String> headers = new HashMap<String, String>();

		int page = 1;
		boolean hasMorePages = false;
		do {
			Map<String, Object> responseMap = null;
			String response = null;

			int counter = 1, maxCouter = 3;
			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
				try {
					String requestStr = request.keySet().stream().map(k -> {
						try {
							return k + "=" + URLEncoder.encode(request.get(k), "utf8");
						} catch (Exception e) {
						}
						return null;
					}).filter(v -> v != null).collect(Collectors.joining("&"));

					String url = StringUtil.concate(getString(apiDetails, "url"), "page=", String.valueOf(page),
							requestStr.isEmpty() ? "" : "&" + requestStr);

					response = sunHttpClient.doGet(url, headers);
					responseMap = JsonUtil.readValue(response, Map.class);

				} catch (UnAuthorizedException e) {
					if (counter++ <= maxCouter)
						unauthorized = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} while (unauthorized);

			if (!isValidResponse(responseMap)) {
				throw new ZohoInvalidResponseException(response);
			}

			List<Map<String, Object>> tmpBills = (List<Map<String, Object>>) getObject(responseMap, "vendorpayments");
			tmpBills.stream().forEach(map -> {
				vendorPayments.add(map);
			});

			Map<String, Object> pageContext = (Map<String, Object>) getMap(responseMap, "page_context");
			hasMorePages = getBooleanValue(pageContext, "has_more_page");

			page++;
		} while (hasMorePages);

		return vendorPayments;
	}

	@Override
	public byte[] getVendorPaymets(String vendorPaymentId) {
		logger.info("getVendorPaymets: vendorPaymentId:{}", vendorPaymentId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("get-vendor-payment");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("vendor_payment_id", vendorPaymentId);

				return sunHttpClient.doGetForBytes(url, headers);
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return null;
	}

	@Override
	public boolean applyCreditNote(ApplyCreditNoteRequest request) {
		logger.info("applyCreditNote: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("apply-credit-notes");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String strRequest = JsonUtil.toString(request);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice_id", request.getInvoiceId());

				String response = sunHttpClient.doPost(url, strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				return true;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return false;
	}

	public Map<String, Object> createItem(ZohoItem request) {
		logger.info("createItem: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("create-item");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String strRequest = JsonUtil.toString(request);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");

				String response = sunHttpClient.doPost(url, strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				Map<String, Object> item = (Map<String, Object>) getObject(responseMap, "item");
				if (item != null) {
					zohoDao.saveZohoItems(Arrays.asList(item));
				}

				return item;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> updateItem(ZohoItem request) {
		logger.info("UpdateItem: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("update-item");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		String strRequest = JsonUtil.toString(request);

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("item-id", request.getItem_id());

				String response = apacheHttpClient.doPut(url, strRequest, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				Map<String, Object> item = (Map<String, Object>) getObject(responseMap, "item");
				if (item != null) {
					zohoDao.updateZohoItems(Arrays.asList(item));
				}

				return item;
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public List<Map<String, Object>> listInvoices(Map<String, String> request) {
		logger.info("listInvoices: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-invoices");

		List<Map<String, Object>> invoices = new ArrayList<>();

		Map<String, String> headers = new HashMap<String, String>();

		int page = 1;
		boolean hasMorePages = false;
		do {
			Map<String, Object> responseMap = null;
			String response = null;

			int counter = 1, maxCouter = 3;
			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
				try {

					String requestStr = request.keySet().stream().map(k -> {
						try {
							return k + "=" + URLEncoder.encode(request.get(k), "utf8");
						} catch (Exception e) {
						}
						return null;
					}).filter(v -> v != null).collect(Collectors.joining("&"));

					String url = StringUtil.concate(getString(apiDetails, "url"), "page=", (page), "&", requestStr);

					response = sunHttpClient.doGet(url, headers);
					responseMap = JsonUtil.readValue(response, Map.class);

				} catch (UnAuthorizedException e) {
					if (counter++ <= maxCouter)
						unauthorized = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} while (unauthorized);

			if (!isValidResponse(responseMap)) {
				throw new ZohoInvalidResponseException(response);
			}

			List<Map<String, Object>> tmpInvoices = (List<Map<String, Object>>) getObject(responseMap, "invoices");
			invoices.addAll(tmpInvoices);

			Map<String, Object> pageContext = (Map<String, Object>) getMap(responseMap, "page_context");
			hasMorePages = getBooleanValue(pageContext, "has_more_page");

			page++;
		} while (hasMorePages);

		return invoices;
	}

	@Override
	public Map<String, Object> refreshInvoice(String invNo) {
		logger.info("refreshInvoice: {}", invNo);

		if (!zohoDao.invoiceRefreshRequired(invNo)) {
			return null;
		}

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("refresh-invoice");

		Map<String, Object> responseMap = null;

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
			try {
				String requestStr = "expiry_time=" + DateUtil.add(new Date(), Calendar.DATE, 31) + "&transaction_id="
						+ invNo;
				String url = StringUtil.concate(getString(apiDetails, "url"), requestStr);
				String response = sunHttpClient.doGet(url, headers);
				responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				responseMap = (Map<String, Object>) getMap(responseMap, "data");
				if (responseMap != null && responseMap.containsKey("share_link")) {
					zohoDao.updateInvoiceUrl(invNo, getString(responseMap, "share_link"));
				}
			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return responseMap;
	}

	@Override
	public Map<String, Object> getInvoiceDetails(String invoiceID) {
		logger.info("checking invoiceDetails of {}", invoiceID);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("get-invoiceJson");

		Map<String, String> headers = new HashMap<String, String>();

		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Bearer " + getString(apiDetails, "access_token"));
			headers.put("Accept", "application/json");

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("invoice_id", invoiceID);

				String response = sunHttpClient.doGet(url, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

				Map<String, Object> einvoiceDetails = (Map<String, Object>) getObject(responseMap, "invoice");
				if (einvoiceDetails != null) {
					return einvoiceDetails;
				}

			} catch (UnAuthorizedException e) {
				unauthorized = true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (unauthorized);

		return Collections.EMPTY_MAP;
	}

	@Override
	public List<Map<String, Object>> listVendorCredit(Map<String, String> request) {
		logger.info("listVendorCredits: {}", request);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("list-credit");

		List<Map<String, Object>> credits = new ArrayList<>();

		Map<String, String> headers = new HashMap<String, String>();

		int page = 1;
		boolean hasMorePages = false;
		do {
			Map<String, Object> responseMap = null;
			String response = null;

			int counter = 1, maxCouter = 3;
			boolean unauthorized = false;
			do {
				if (unauthorized)
					refreshToken(apiDetails);

				unauthorized = false;

				headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));
				try {

					String requestStr = request.keySet().stream().map(k -> {
						try {
							return k + "=" + URLEncoder.encode(request.get(k), "utf8");
						} catch (Exception e) {
						}
						return null;
					}).filter(v -> v != null).collect(Collectors.joining("&"));

					String url = StringUtil.concate(getString(apiDetails, "url"), "page=", (page), "&", requestStr);

					response = sunHttpClient.doGet(url, headers);
					responseMap = JsonUtil.readValue(response, Map.class);

				} catch (UnAuthorizedException e) {
					if (counter++ <= maxCouter)
						unauthorized = true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} while (unauthorized);

			if (!isValidResponse(responseMap)) {
				throw new ZohoInvalidResponseException(response);
			}

			List<Map<String, Object>> tmpCredits = (List<Map<String, Object>>) getObject(responseMap, "vendor_credits");
			credits.addAll(tmpCredits);

			Map<String, Object> pageContext = (Map<String, Object>) getMap(responseMap, "page_context");
			hasMorePages = getBooleanValue(pageContext, "has_more_page");

			page++;
		} while (hasMorePages);

		return credits;
	}

	@Override
	public void sendPayAdviceEmail(Long paymentId, String request) {
		logger.info("sendPayAdviceEmail: {}", paymentId);

		Map<String, Object> apiDetails = zohoDao.getZohoApiDetails("send-email");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json;charset=UTF-8");

		int counter = 1, maxCouter = 3;
		boolean unauthorized = false;
		do {
			if (unauthorized)
				refreshToken(apiDetails);

			unauthorized = false;

			headers.put("Authorization", "Zoho-oauthtoken " + getString(apiDetails, "access_token"));

			try {
				String url = getString(apiDetails, "url");
				url = url.replace("X", String.valueOf(paymentId));

				String response = sunHttpClient.doPost(url, request, headers);
				Map<String, Object> responseMap = JsonUtil.readValue(response, Map.class);

				if (!isValidResponse(responseMap)) {
					throw new ZohoInvalidResponseException(response);
				}

			} catch (UnAuthorizedException e) {
				if (counter++ <= maxCouter)
					unauthorized = true;
			} catch (ZohoInvalidResponseException e) {
				logger.error(
						"Error occurred while sending payadvice email-ZohoInvalidResponseException, paymentId = {}, error = {}",
						paymentId, e);
				throw e;
			} catch (Exception e) {
				logger.error("Error occurred while sending payadvice email-Exception, paymentId = {}, error = {}",
						paymentId, e);
				throw e;
			}
		} while (unauthorized);
	}
}
