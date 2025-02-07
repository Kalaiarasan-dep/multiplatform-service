package in.hashconnect.service;

import in.hashconnect.excel.ExcelBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getString;

public class InvoiceExportService {
    private List<Map<String, Object>> orders;
    String[] headers = { "Request Id","GST Number","Invoice Requested Amount","Status","Program","Batch","Month","Partner","Order count",
            "Invoice Date","Invoice No","Cost","Gst Amount","Link of the Invoice","Invoice Requested Date","Invoice Submitted Date","Invoice Approved Date","Invoice Paid Date","UTR No","Transaction Date" };

    private ExcelBuilder builder = null;
    private HttpServletResponse response = null;

    public InvoiceExportService(List<Map<String, Object>> orders, HttpServletResponse response) {
        this.orders = orders;
        this.response = response;

        builder = new ExcelBuilder(50);
        builder.createSheet("All_invoices_");
        builder.createRow(0);

        builder.addBoldHeaders(headers);
    }

    public void writeRows() throws Exception {
        int row = 1;
        for (Map<String, Object> order : orders) {
            Object[] columnValues = {
                    getString(order, "Request Id"),
                    getString(order, "GST Number"),
                    getString(order, "Invoice Requested Amount"),
                    getString(order, "Status"),
                    getString(order, "Program"),
                    getString(order, "Batch"),
                    getString(order, "Month"),
                    getString(order, "Partner"),
                    getString(order, "Order count"),
                    getString(order, "invoice_date"),
                    getString(order, "invoice_number"),
                    getDouble(order, "Cost"),
                    getDouble(order, "Gst Amount"),
                    getString(order, "inv_img_link"),
                    getString(order, "Requested Date"),
                    getString(order, "invoice_submitted_date"),
                    getString(order, "invoice_approved_date"),
                    getString(order, "invoice_paid_date"),
                    getString(order, "utr_no"),
                    getString(order, "transaction_date")
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

