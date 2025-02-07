package in.hashconnect.service;

import in.hashconnect.excel.ExcelBuilder;
import jakarta.servlet.http.HttpServletResponse;
import static org.apache.commons.collections4.MapUtils.*;
import java.util.List;
import java.util.Map;

public class OrdersExportService {
    private List<Map<String, Object>> orders;
    String[] headers = { "HC Ticket ID", "Request ID for Invoice Requested Orders", "Status", "Program", "Month", "Batch", "Order ID", "Order Date", "Partner Name", "Merchant ID", "Contract ID", "Cost", "Uploaded Date", "Remarks" };

    private ExcelBuilder builder = null;
    private HttpServletResponse response = null;

    public OrdersExportService(List<Map<String, Object>> orders, HttpServletResponse response) {
        this.orders = orders;
        this.response = response;

        builder = new ExcelBuilder(50);
        builder.createSheet("All_orders_");
        builder.createRow(0);

        builder.addBoldHeaders(headers);
    }

    public void writeRows() throws Exception {
        int row = 1;
        for (Map<String, Object> order : orders) {
            Object[] columnValues = {
                    getString(order, "hcTicketID"),
                    getString(order, "requestId"),
                    getString(order, "status"),
                    getString(order, "program"),
                    getString(order, "month"),
                    getString(order, "batch"),
                    getString(order, "orderId"),
                    getString(order, "orderDate"),
                    getString(order, "partnerName"),
                    getString(order, "merchantId"),
                    getString(order, "contractId"),
                    getDouble(order, "cost"),
                    getString(order, "uploadedDate"),
                    getString(order, "remarks")
            };

            builder.createRow(row++).addRowDataWithTypes(columnValues);
        }
        // auto sizing columns
        builder.autoSizeColumn(headers.length);

        // write excel content to response
        builder.writeToOutputStream(this.response.getOutputStream());
    }

    private String getString(Map<String, Object> map, String key) {
        return map.get(key) != null ? map.get(key).toString() : "";
    }

    private Double getDouble(Map<String, Object> map, String key) {
        try {
            return map.get(key) != null ? Double.parseDouble(map.get(key).toString()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
