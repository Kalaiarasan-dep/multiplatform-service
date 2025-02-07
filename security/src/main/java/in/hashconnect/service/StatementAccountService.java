package in.hashconnect.service;

import static in.hashconnect.util.StringUtil.concate;
import static org.apache.commons.collections4.MapUtils.getLong;
import static org.apache.commons.collections4.MapUtils.getString;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.excel.ExcelBuilder;
import in.hashconnect.util.BillsVo;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SOASummary;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class StatementAccountService {
	private static final Logger logger = LoggerFactory.getLogger(StatementAccountService.class);
	@Autowired
	private PartnerDao partnerDao;
	@Autowired
	private SettingsUtil settingsUtil;
	@Autowired
	private PartnerService partnerService;
	
	private static final String INV_APPROVED = "Invoice Approved";
	private static final String TXN_COMPLETD = "Transaction Completed";
	private static final String TXN_ADJUSTED = "Transaction Adjusted";
	private static final String CR_NOTES = "Credit Notes";
	private static final String SOA_DEDUCTI0N = "Soa Deduction";
	private static final String RUPEE_SYMBOL = "₹";
	private static final String SOA_EXCEL_PREFIX = "attachment; filename=SOA";
	private static final String EXCEL_CONTENT_TYPE = "application/octet-stream";
	private static final String EXCEL_EXT = ".xlsx";
	private static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
	private ExcelBuilder builder = null;
      	
	public void generateSOAReport(String userId, HttpServletResponse response)  {
		builder = new ExcelBuilder(50);
		try {
			Map<String, Object> partnerMap = partnerDao.getPartnerId(userId);
			String partnerId = MapUtils.getString(partnerMap, "partner_id");
			String partnerName = MapUtils.getString(partnerMap, "registered_business_name");
			
			String fileName = concate(SOA_EXCEL_PREFIX,"_",formatPartnerName(partnerName), "_",
					DateUtil.format("yyyy-MM-dd_HH_mm_ss", new Date()), EXCEL_EXT);
			response.setContentType(EXCEL_CONTENT_TYPE);
			response.setHeader("Content-Disposition", fileName);
			SOASummary summary = new SOASummary();
			generateIndex(builder);
			generateSummary(partnerId, builder);
			createProfileDetails(partnerId, builder);
			generateAccountedInvoices(partnerId, builder, summary);
			generateBillsYetToAccount(partnerId, builder);
			generateOrderWiseDetails(partnerId, builder);
			genereateOrderSummary(partnerId, builder);
			generateStatusTerminology(partnerId, builder);
			
			builder.writeToOutputStream(response.getOutputStream());
		} catch (Exception e) {
			logger.error("Error occurred during soa report generation", e);
		}
		
	}
	
	private void generateAccountedInvoices(String partnerId, ExcelBuilder builder,
			SOASummary summary) {
		
		String headerNames = settingsUtil.getValue("soa_accounted_inv");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Accounted Invoices");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
		int idxRow = 1;
		
		List<BillsVo> bills =  partnerService.getBillReport(partnerId, summary);
		
		for (BillsVo b : bills) {
			builder.createRow(idxRow++).addRowDataWithTypes(b.getBookTime(), b.getBillNumber(), b.getSubTotal(),
					b.getTaxTotal(), b.getTotalWithOutTds(), b.getTotal(), b.getStatus(), b.getDivision(),
					b.getPaySummary(), b.getInvoiceRequestId());
		}
		builder.autoSizeColumn(strings.length);
	}
	
	private void generateSummary(String partnerId, ExcelBuilder builder) {
		builder.createSheet("Summary");
		int idxRow = 0;
        
        Map<String, BigDecimal> map = partnerDao.getSummaryForSOA(partnerId);
        BigDecimal invAccounted = map.get("invaccounted");
        BigDecimal paidAmount = map.get("paidAmount");
        BigDecimal crNotes = map.get(CR_NOTES);
        BigDecimal netInvAccounted = invAccounted.subtract(crNotes);
        BigDecimal outStandingAmt = netInvAccounted.subtract(paidAmount);
        		
        builder.createRow(idxRow++).addBoldHeaders("As Per Statement Of Account", "Amount in Rs.");
        builder.createRow(idxRow++).addRowDataWithTypes("Opening Balance  as on 31st March",StringUtil.concate(RUPEE_SYMBOL, map.get("balance")));
        builder.createRow(idxRow++).addRowDataWithTypes("Invoice Accounted",StringUtil.concate(RUPEE_SYMBOL, invAccounted));
        builder.createRow(idxRow++).addRowDataWithTypes("Less: Credit Note Recd.",StringUtil.concate(RUPEE_SYMBOL,crNotes));
        builder.createRow(idxRow++).addBoldHeaders("Net Invoices Accounted - A",StringUtil.concate(RUPEE_SYMBOL, netInvAccounted));
        builder.createRow(idxRow++).addRowDataWithTypes("", "");
        builder.createRow(idxRow++).addRowDataWithTypes("Paid Amount(inclusive TDS)",StringUtil.concate(RUPEE_SYMBOL, paidAmount));
        builder.createRow(idxRow++);
        builder.createRow(idxRow++).addBoldHeaders("Oustanding Payable by Hash Connect Against Accounted Invoices",
        		StringUtil.concate(RUPEE_SYMBOL, outStandingAmt));
       
		builder.autoSizeColumn(2);
	}
	
	@SuppressWarnings("unchecked")
	private void generateIndex(ExcelBuilder builder) {
		String headerNames = settingsUtil.getValue("soa_index_header");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Index");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
        String soaIndexVal = settingsUtil.getValue("soa_index_values");
        Map<String,Object> map = JsonUtil.readValue(soaIndexVal, Map.class);
        int row = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
        	builder.createRow(row++).addRowDataWithTypes(entry.getKey(), entry.getValue());
        }
        
        builder.autoSizeColumn(strings.length);
	}
	
	@SuppressWarnings("unchecked")
	private void generateStatusTerminology(String partnerId, ExcelBuilder builder) {
		String headerNames = settingsUtil.getValue("soa_status_terminology_headers");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Status Terminology");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
        String terminlogyValues = settingsUtil.getValue("soa_status_terminology");
        Map<String, Map<String,Object>> map = JsonUtil.readValue(terminlogyValues, Map.class);
        int row = 1;
       for (Map.Entry<String, Map<String,Object>> entry: map.entrySet()) {
    	   String section = entry.getKey();
    	   Map<String, Object> val = entry.getValue();
    	   	for (Map.Entry<String, Object> valMap : val.entrySet()) {
    	   		Object[] columnValues = { section, valMap.getKey(), valMap.getValue()};
    	   		builder.createRow(row++).addRowDataWithTypes(columnValues);
    	 }
        }
       builder.autoSizeColumn(strings.length);
 	}
	
	@SuppressWarnings("unchecked")
	private void genereateOrderSummary(String partnerId, ExcelBuilder builder) {
		String headerNames = settingsUtil.getValue("soa_order_summary");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Order Summary");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
        List<Map<String, Object>> statusSummary = partnerDao.getSOAOrderSummary(partnerId);
        int row = 1;
        for (Map<String, Object> status : statusSummary) {
            Object[] columnValues = { getString(status, "status_name"), getString(status, "no_of_orders"),
            		getLong(status, "claim_amt")};
            
            builder.createRow(row++).addRowDataWithTypes(columnValues);
        }
        builder.autoSizeColumn(strings.length);
		
	}
	
	@SuppressWarnings("unchecked")
	private void generateOrderWiseDetails(String partnerId, ExcelBuilder builder) {
		String headerNames = settingsUtil.getValue("soa_order_dtl");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Order Wise Details");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
        builder.autoSizeColumn(strings.length);
        List<Map<String, Object>> orders = partnerDao.getSOAOrderDetails(partnerId);
        int row = 1;
        for (Map<String, Object> order : orders) {
            Object[] columnValues = { getString(order, "id"), formatOrderDate(getString(order, "order_date")),
                    getString(order, "program_name"), getString(order, "status_name"),
                    getString(order, "claimed_amount"), getString(order, "upload_batch_id"), 
                    getString(order, "batch_month")};

            builder.createRow(row++).addRowDataWithTypes(columnValues);
        }
        
		
	}
	
	@SuppressWarnings("unchecked")
	private void generateBillsYetToAccount(String partnerId, ExcelBuilder builder) {
		String headerNames = settingsUtil.getValue("soa_bills_yet_to_account");
		List<String> headerList = JsonUtil.readValue(headerNames, List.class);
		String[] strings = headerList.stream().toArray(String[]::new);
		builder.createSheet("Bills Yet to Account");
        builder.createRow(0);
        builder.addBoldHeaders(strings);
        List<Map<String, Object>> invoices = partnerDao.getSOABillsYetToAccount(partnerId);
        int row = 1;
        for (Map<String, Object> invoice : invoices) {
            Object[] columnValues = { getString(invoice, "partner_invoice_request_id"), getString(invoice, "create_dt"),
                    getString(invoice, "program_name"), getString(invoice, "invoice_submitted_dt"),getString(invoice, "invoice_date"), 
                    getString(invoice, "invoice_number"),getString(invoice,"invoice_submitted_amount"),
                    getString(invoice, "status_name"), getString(invoice, "soa_remarks") };

            builder.createRow(row++).addRowDataWithTypes(columnValues);
        }
        builder.autoSizeColumn(strings.length);
        
	}
	
	private void createProfileDetails(String partnerId, ExcelBuilder builder) throws IOException {
		
		
		builder.createSheet("Profile Details");
        builder.createRow(0);
        Map<String, Object> map = partnerDao.getPartnerProfileDetails(partnerId);
		String mask_char = settingsUtil.getValue("id_mask_char");
        int idxRow = 0;
        String contractId = getString(map, "contract_id");
        String merchantId = getString(map, "merchant_id");
        if (StringUtil.isValid(contractId) && StringUtil.isValid(mask_char)) {
        	contractId = mask_char.repeat(contractId.length());
        }
        if (StringUtil.isValid(merchantId) && StringUtil.isValid(mask_char)) {
        	merchantId = mask_char.repeat(merchantId.length());
        }
        builder.createRow(idxRow++).addRowDataWithTypes("Parent Store ID",getString(map, "id"));
		builder.createRow(idxRow++).addRowDataWithTypes("GST #",getString(map, "gst_number"));
		builder.createRow(idxRow++).addRowDataWithTypes("Contract #",contractId);
		builder.createRow(idxRow++).addRowDataWithTypes("Merchant ID",merchantId);
		builder.createRow(idxRow++).addRowDataWithTypes("Store Name",getString(map, "registered_business_name"));
		builder.createRow(idxRow++).addRowDataWithTypes("Address",getString(map, "address"));
		builder.createRow(idxRow++).addRowDataWithTypes("Owner Email",getString(map, "owner_email"));
		builder.createRow(idxRow++).addRowDataWithTypes("Contact Person",getString(map, "owner_name"));
		builder.createRow(idxRow++).addRowDataWithTypes("Contact Mobile",getString(map, "mobile"));
		builder.createRow(idxRow++).addRowDataWithTypes("Partner Login ID",getString(map, "partner_login_id"));
		builder.createRow(idxRow++).addRowDataWithTypes("Bank Name", getString(map, "bank_name"));
		builder.createRow(idxRow++).addRowDataWithTypes("Beneficiary Name",getString(map, "beneficiary_name"));
		builder.createRow(idxRow++).addRowDataWithTypes("Account Number",getString(map, "account_number"));
		builder.createRow(idxRow++).addRowDataWithTypes("IFSC",getString(map, "ifsc_code"));
		builder.createRow(idxRow++).addRowDataWithTypes("Store Status",getString(map, "store_status"));
		builder.createRow(idxRow++).addRowDataWithTypes("User Status",getString(map, "user_status"));
		builder.createRow(idxRow++).addRowDataWithTypes("Login Validity","");//need to add once zoho integrated
		builder.createRow(idxRow++).addRowDataWithTypes("","");
		builder.createRow(idxRow++).addRowDataWithTypes(settingsUtil.getValue("soa_profile_note"),"");
		
		builder.autoSizeColumn(2);
	}
	
	private String formatPartnerName(String partnerNm) {
		if (StringUtil.isValid(partnerNm)) {
			return StringUtil.removeWhiteSpaces(partnerNm);
		}
		return partnerNm;
	}
	
	private String formatOrderDate(String date) {
	    try {
			Date dt = inputFormat.parse(date);
			return outputFormat.format(dt);
		} catch (Exception e) {
			//ignore it
		}
	   	return date;
	}
}
