package in.hashconnect.zoho.dao;

import java.util.List;
import java.util.Map;

import in.hashconnect.zoho.vo.CreateCustomerRequest;
import in.hashconnect.zoho.vo.CreateInvoiceRequest;
import in.hashconnect.zoho.vo.CreatePaymentRequest;
import in.hashconnect.zoho.vo.RefundPaymentRequest;

public interface ZohoDao {
	Map<String, Object> getZohoApiDetails(String string);

	void saveZohoItems(List<Map<String, Object>> items);

	void updateZohoItems(final List<Map<String, java.lang.Object>> items);

	void updateZohoToken(String accessToken);

	void createCustomer(CreateCustomerRequest request, Map<String, Object> response);

	void createInvoice(CreateInvoiceRequest request, Map<String, Object> response);

	void createPayment(CreatePaymentRequest request, Map<String, Object> response);

	void updatePayment(CreatePaymentRequest request, Map<String, Object> response);

	void createRefund(RefundPaymentRequest request, Map<String, Object> responseMap);

	void updateCustomer(CreateCustomerRequest customerData, Map<String, Object> responseMap);

	void updateInvoice(CreateInvoiceRequest invoice, Map<String, Object> response);

	void createCreditNote(CreateInvoiceRequest creditNote, Map<String, Object> response);

	public Long deletePaymentFromInvoice(Long invoiceDeletePaymentId, Long invoiceId, String zohoInvoiceId,
			String zohoInvoicePaymentId, Map<String, Object> responseMap);

	void updateCustomer(Long customerId, Map<String, Object> response);

	public boolean canDownloadReviews();

	void truncateZohoItem();

	void deletePaymentFromCustomer(Long paymentId);

	void updateInvoiceUrl(String invId, String string);

	boolean invoiceRefreshRequired(String invId);
}
