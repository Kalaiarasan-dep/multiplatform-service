package in.hashconnect.vo;

import static in.hashconnect.util.StringUtil.convertToString;
import static org.apache.commons.collections4.MapUtils.getFloatValue;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.collections4.MapUtils.getInteger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.service.TaxCalculator;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;

/**
 * This is to check the partner invoice validation. check the amount from the requested amount against 
 * partner entered amount
 */
@Component
public class PartnerInvoiceValidatorImpl implements PartnerInvoiceValidator{
	private static final Logger logger = LoggerFactory.getLogger(PartnerInvoiceValidatorImpl.class);
	@Autowired
	private PartnerDao partnerDao;
	@Autowired
	private SettingsUtil settingsUtil;
	@Autowired
	private GenericDao genericDao;
	@Autowired
	private TaxCalculator taxCalculator;

	@SuppressWarnings("unchecked")
	@Override
	public PartnerResponse validate(Map<String, Object> dtls) {
		PartnerResponse resp = new PartnerResponse();
		List<String> validationResps = new ArrayList<String>();
		String validationMsgs = settingsUtil.getValue("partner_validation");
		Map<String, Object> msgsMap = JsonUtil.readValue(validationMsgs, Map.class);
		
		if (!StringUtil.isValid((String)dtls.get("invReqId"))) {
			validationResps.add(getString(msgsMap, UploadFileConstants.INVOICE_NO_CANNOT_EMPTY));
			return resp;
		}
		
		Map<String, Object> partner_map = genericDao.getPartnerIdByInvoiceNo(Integer.valueOf((String)dtls.get("invReqId")));
		if (partnerDao.isInvoiceExistInFinancialYrForPartner(getString(dtls, "invoiceNo"),
				getInteger(partner_map, "partner_id"), Long.valueOf(getString(dtls, "invReqId")))) {
			validationResps.add(getString(msgsMap, UploadFileConstants.INVOICE_ALREADY_EXISTS));
			resp.setErrors(validationResps);
		}
		
		if (isGreatherThanToday((String)dtls.get("invoiceDt"))) {
			validationResps.add(getString(msgsMap, UploadFileConstants.INVOICE_DATE));
		}
		
		if (StringUtil.isValid((String)dtls.get("invoiceAmt")) && 
				((String)dtls.get("invoiceAmt")).length() > 15) {
			validationResps.add(getString(msgsMap, UploadFileConstants.INVOICE_AMT));
		}
		
		
		String taxType = genericDao.getTaxType((Integer)partner_map.get("partner_id"));
		
		//These values from input request
		String invoiceAmt = StringUtil.isValid(convertToString(dtls.get("invoiceAmt")))?convertToString(dtls.get("invoiceAmt")):"0";
		BigDecimal partnerEnteredAmt = new BigDecimal(StringUtil.convert(invoiceAmt, Double.class));
		
		Float igstValue = getFloatValue(dtls, "igst");
		Float cgstValue = getFloatValue(dtls, "cgst");
		Float sgstValue = getFloatValue(dtls, "sgst");
		partnerEnteredAmt = partnerEnteredAmt.setScale(2, RoundingMode.HALF_EVEN);
		
		BigDecimal requestedInvAmt = new BigDecimal((Float)(partner_map.get("total_amount")));
		requestedInvAmt = requestedInvAmt.setScale(2, RoundingMode.HALF_EVEN);
		
		String errorMsg = getErrorMessage(taxType, cgstValue, sgstValue, igstValue,
				requestedInvAmt, partnerEnteredAmt, partner_map);
		if (errorMsg.length() > 0) {
			validationResps.add(errorMsg);
		}
		resp.setErrors(validationResps);
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	private String getErrorMessage(String taxType, Float cgst, Float 
			sgst, Float igst, BigDecimal requestedAmt, BigDecimal partnerEnteredAmt,
			Map<String, Object> partner_map) {
		Float dbIgstValue = getFloatValue(partner_map, "igst_rate");
		Float dbCgstValue = getFloatValue(partner_map, "cgst_rate");
		Float dbSgstValue = getFloatValue(partner_map, "sgst_rate");
		StringBuilder errorMsg = new StringBuilder();
		String taxErrMsgs = settingsUtil.getValue("partner_tax_msg");
		Map<String, Object> msgsMap = JsonUtil.readValue(taxErrMsgs, Map.class);
		
		String taxAdjustNumbers = settingsUtil.getValue("tax_adjust_values");
		Map<String, Object> taxMap = JsonUtil.readValue(taxAdjustNumbers, Map.class);
		int taxAdjust_CS_GST = (Integer)taxMap.get("CS_GST");
		int taxAdjust_IGST = (Integer)taxMap.get("IGST");
		int taxAdjust_TOTAL = (Integer)taxMap.get("total_variation");

		Map<String, BigDecimal> map = taxCalculator.calculate(taxType, requestedAmt.toString());
		if ("CS_GST".equalsIgnoreCase(taxType)) {
			double tcgst = ((BigDecimal)map.get("CGST")).doubleValue();
			if ((tcgst) <= (cgst - taxAdjust_CS_GST) || (tcgst) >= (cgst + taxAdjust_CS_GST)) {
				errorMsg.append(msgsMap.get("cgstMsg"));
				errorMsg.append(dbCgstValue);
				errorMsg.append(msgsMap.get("recheckMsg"));
			}
			
			double tsgst = ((BigDecimal)map.get("SGST")).doubleValue();
			if ((tsgst) <= (sgst - taxAdjust_CS_GST) || (tsgst) >= (sgst + taxAdjust_CS_GST)) {
				errorMsg.append(msgsMap.get("sgstMsg"));
				errorMsg.append(dbSgstValue);
				errorMsg.append(msgsMap.get("recheckMsg"));
			}
			partnerEnteredAmt = partnerEnteredAmt.add(BigDecimal.valueOf(sgst));
			partnerEnteredAmt = partnerEnteredAmt.add(BigDecimal.valueOf(cgst));
			
			requestedAmt = requestedAmt.add(BigDecimal.valueOf(dbSgstValue));
			requestedAmt = requestedAmt.add(BigDecimal.valueOf(dbCgstValue));
		}
		if ("IGST".equalsIgnoreCase(taxType)) {
			double tigst = ((BigDecimal)map.get("IGST")).doubleValue();
			if ((tigst) <= (igst - taxAdjust_IGST) || (tigst) >= (igst + taxAdjust_IGST)) {
				errorMsg.append(msgsMap.get("igstMsg"));
				errorMsg.append(dbIgstValue);
				errorMsg.append(msgsMap.get("recheckMsg"));
			}
			partnerEnteredAmt = partnerEnteredAmt.add(BigDecimal.valueOf(igst));
			requestedAmt = requestedAmt.add(BigDecimal.valueOf(dbIgstValue));
		}
		
		partnerEnteredAmt = partnerEnteredAmt.setScale(2, RoundingMode.HALF_EVEN);
		requestedAmt = requestedAmt.setScale(2, RoundingMode.HALF_EVEN);
		
		BigDecimal dbInvAmt = new BigDecimal((Float)(partner_map.get("total_amount")));
		dbInvAmt = dbInvAmt.setScale(2, RoundingMode.HALF_EVEN);
		
		if (requestedAmt.doubleValue() <= (partnerEnteredAmt.doubleValue() - taxAdjust_TOTAL)
				|| requestedAmt.doubleValue() >= (partnerEnteredAmt.doubleValue() + taxAdjust_TOTAL)) {
				errorMsg.append(msgsMap.get("invMsg"));
				errorMsg.append(dbInvAmt);
				errorMsg.append(". ");
				errorMsg.append(msgsMap.get("totalMsg"));
				errorMsg.append(requestedAmt);
				errorMsg.append(msgsMap.get("recheckMsg"));
		}
		return errorMsg.toString();
	}
	
	private boolean isGreatherThanToday(String date) {
		try {
			return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .isAfter(LocalDate.now());
		} catch(Exception e) {
			logger.error("Error occurred during date parsing "+e);
		}
	    return false;
	}
}