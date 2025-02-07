package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.getString;

import java.util.List;
import java.util.Map;

import in.hashconnect.excel.ExcelBuilder;
import jakarta.servlet.http.HttpServletResponse;

public class InvoiceOrdersExportService {
	private List<Map<String, Object>> invOrders;
	String[] headers = { "Program", "Month", "Order Id", "Cost" };

	private ExcelBuilder builder = null;
	private HttpServletResponse response = null;
	private double totalCostGST = 0.0;

	public InvoiceOrdersExportService(List<Map<String, Object>> invOrders, HttpServletResponse response) {
		this.invOrders = invOrders;
		this.response = response;

		builder = new ExcelBuilder(50);
		builder.createSheet("invoice-orders");
		builder.createRow(0);
		builder.addBoldHeaders(headers);
	}

	public void writeRows() throws Exception {
		int row = 1;
		for (Map<String, Object> order : invOrders) {
			Object[] columnValues = {
					getString(order, "Program"),
					getString(order, "Month"),
					getString(order, "Order Id"),
					getDouble(order, "Cost")
			};

			builder.createRow(row++).addRowDataWithTypes(columnValues);
		}

		Object[] totalRow = new Object[headers.length];
		builder.createRow(row).addRowDataWithTypes(totalRow);

		builder.autoSizeColumn(headers.length);
		builder.writeToOutputStream(this.response.getOutputStream());
	}

	private String getString(Map<String, Object> map, String key) {
		Object value = map.get(key);
		return value != null ? value.toString() : "";
	}

	private Double getDouble(Map<String, Object> map, String key) {
		Object value = map.get(key);
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else {
			try {
				return value != null ? Double.parseDouble(value.toString()) : 0.0;
			} catch (NumberFormatException e) {
				return 0.0;
			}
		}
	}
}

