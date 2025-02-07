package in.hashconnect.service;

import in.hashconnect.excel.ExcelBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public class PartnersMasterExportService {
    private List<Map<String, Object>> masters;
    String[] headers = { "Partner ID","Store Category", "Business Type", "Business Name", "Owner Name", "Owner Email", "Owner Mobile Number", "Finance Person Name", "Finance Person Mobile Number", "Lenovo Contract Number", "Business State ", "Business Address 1", "Business Address 2", "City", "ZIP Code",
    "Business PAN Number","GST Type","GST Number","Bank Name","Branch","Account Number","Account Type","IFSC Code","Beneficiary Name","UPI ID","Cancelled Cheque Copy","PAN Card Copy","GST Certificate","Status","User ID","Vendor ID","Active/Inactive Status","Remarks"};

    private ExcelBuilder builder = null;
    private HttpServletResponse response = null;

    public PartnersMasterExportService(List<Map<String, Object>> masters, HttpServletResponse response) {
        this.masters = masters;
        this.response = response;

        builder = new ExcelBuilder(50);
        builder.createSheet("Partners_Master_");
        builder.createRow(0);

        builder.addBoldHeaders(headers);
    }

    public void writeRows() throws Exception {
        int row = 1;
        for (Map<String, Object> master : masters) {
            Object[] columnValues = {
                    getString(master, "partner_id"),
                    getString(master, "store_type"),
                    getString(master, "business_type"),
                    getString(master, "registered_business_name"),
                    getString(master, "owner_name"),
                    getString(master, "owner_email"),
                    getString(master, "owner_mobile"),
                    getString(master, "finance_head_name"),
                    getString(master, "finance_head_mobile"),
                    getString(master, "contract_id"),
                    getString(master, "partner_state"),
                    getString(master, "address_line_1"),
                    getDouble(master, "address_line_2"),
                    getString(master, "partner_city"),
                    getString(master, "pincode"),
                    getString(master, "pan_number"),
                    getString(master, "gst_type"),
                    getString(master, "gst_number"),
                    getString(master, "bank"),
                    getString(master, "branch_name"),
                    getString(master, "account_number"),
                    getString(master, "account_type"),
                    getString(master, "ifsc_code"),
                    getString(master, "beneficiary_name"),
                    getString(master, "upi_id"),
                    getString(master, "cancelled_cheque_url"),
                    getString(master, "pancard_url"),
                    getString(master, "gst_url"),
                    getString(master, "partner_status"),
                    getString(master, "user_id"),
                    getString(master, "zoho_vendor_id"),
                    getString(master, "user_status")
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
