package in.hashconnect.zoho;

import java.util.List;
import java.util.Map;

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

/**
 * This processor to conenct to Zoho APIs API details -
 * https://www.zoho.com/books/api/v3
 * 
 */
public interface ZohoApiProcessor {
	public void getItems();

	public void downloadItemsDaily();

	public Map<String, Object> createInvoice(CreateInvoiceRequest invoiceData);

	public Map<String, Object> createContact(CreateCustomerRequest invoiceData);

	public Map<String, Object> updateContact(CreateCustomerRequest invoiceData);

	public Map<String, Object> updateContactPerson(ContactPerson request);

	public Map<String, Object> createPayment(CreatePaymentRequest payment);

	public Map<String, Object> updatePayment(CreatePaymentRequest paymentData);

	public boolean applyCreditNote(ApplyCreditNoteRequest request);

	public Map<String, Object> refundPayment(RefundPaymentRequest request);

	public Map<String, Object> listPayments(Map<String, String> request);

	public Map<String, Object> updateInvoice(CreateInvoiceRequest invoice);

	public Map<String, Object> approveInvoice(ApproveInvoiceRequest invoiceId);

	public Map<String, Object> voidInvoice(ApproveInvoiceRequest invoiceId);

	public Map<String, Object> createCreditNote(CreateInvoiceRequest creditNote);

	public Map<String, Object> deleteInvoicePayment(Long invoiceId, String zohoInvoiceId, String zohoInvoicePaymentId);

	public Map<String, Object> deleteCustomerPayment(Long paymentId, String customerPaymentId);

	public Map<String, Object> updateInvoiceAddress(Long invoiceId, String zohoInvoiceId, String type,
			BillingAddress billingAddress);

	public Map<String, Object> deleteInvoice(Long invoiceId, String zohoInvoiceId);

	public Map<String, Object> getCustomer(Long customerId, Map<String, Object> queryParams);

	public Map<String, Object> createBill(CreateBillRequest request);

	public Map<String, Object> updateBill(CreateBillRequest billRequest);

	public List<Map<String, Object>> listBills(Map<String, String> request);

	public Map<String, Object> getBill(String billId);

	public List<Map<String, Object>> listVendorPaymets(Map<String, String> request);

	public byte[] getVendorPaymets(String vendorPaymentId);

	public Map<String, Object> createItem(ZohoItem item);

	public Map<String, Object> updateItem(ZohoItem request);

	public List<Map<String, Object>> listInvoices(Map<String, String> request);

	public Map<String, Object> refreshInvoice(String invNo);

	public Map<String, Object> getInvoiceDetails(String invoiceId);
	
	public List<Map<String, Object>> listVendorCredit(Map<String, String> request);
	
	void sendPayAdviceEmail(Long paymentId, String request);
}
