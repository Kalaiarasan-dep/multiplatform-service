package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;

@SuppressWarnings("unchecked")
@Component
public class TaxCalculator {

	@Autowired
	private SettingsUtil settingsUtil;

	public Map<String, BigDecimal> calculate(String type, String amount) {
		Map<String, BigDecimal> results = new HashMap<String, BigDecimal>();

		String taxValues = settingsUtil.getValue("tax_values");

		Map<String, Object> taxes = JsonUtil.readValue(taxValues, Map.class);

		Float ugstValue = getFloat(taxes, "UGST");
		Float igstValue = getFloat(taxes, "IGST");
		Float cgstValue = getFloat(taxes, "CGST");
		Float sgstValue = getFloat(taxes, "SGST");

		if (type.equals("UGST")) {
			BigDecimal calculatedUGST = calculate(amount, ugstValue);

			results.put("UGST", calculatedUGST);

			return results;
		}
		if (type.equals("IGST")) {
			BigDecimal calculatedIGST = calculate(amount, igstValue);

			results.put("IGST", calculatedIGST);

			return results;
		}

		BigDecimal price = new BigDecimal(amount);

		Float totalTax = cgstValue + sgstValue + 100;
		//price = price.multiply(new BigDecimal(100)).divide(new BigDecimal(totalTax), 2, RoundingMode.HALF_EVEN);

		BigDecimal calculatedCGST = price.multiply(new BigDecimal(cgstValue)).divide(new BigDecimal(100), 2,
				RoundingMode.HALF_EVEN);
		BigDecimal calculatedSGST = price.multiply(new BigDecimal(sgstValue)).divide(new BigDecimal(100), 2,
				RoundingMode.HALF_EVEN);

		results.put("CGST", calculatedCGST);
		results.put("SGST", calculatedSGST);

		return results;
	}

	private BigDecimal calculate(String amount, Float taxValue) {
		Float totalTax = taxValue + 100;

		BigDecimal price = new BigDecimal(amount);
		//price = price.multiply(new BigDecimal(100)).divide(new BigDecimal(totalTax), 2, RoundingMode.HALF_EVEN);

		BigDecimal calculatedIGST = price.multiply(new BigDecimal(taxValue)).divide(new BigDecimal(100), 2,
				RoundingMode.HALF_EVEN);
		return calculatedIGST;
	}

	public static void main(String[] args) {
		TaxCalculator tc = new TaxCalculator();
		BigDecimal bd = tc.calculate("20", 18f);
		System.out.println(bd);

	}
}
