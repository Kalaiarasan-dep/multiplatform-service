package in.hashconnect.service;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.*;

@Service
public class InvoiceInfoPostProcessor implements CommonApiPostProcessor {
    @Autowired
    private SettingsUtil settingsUtil;
    @Autowired
    private GenericDao genericDao;

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if (response.getStatus() == Response.STATUS.FAILED) {
            return response;
        }
        String invId = (String) getObject(request, "id");

        GetResponse res = (GetResponse) response;
        List<Map<String, Object>> records = res.getRecords();

        LocalDate id10Date = null;
        LocalDate id7Date = null;
        LocalDate id14Date = null;
        LocalDate id8Date=null;

        // Find dates for id 10, id 7, and id 14 if they exist
        for (Map<String, Object> record : records) {

            Long id = (Long) record.get("id");


            String dateString = (String) record.get("date");
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (id == 10) {
                id10Date = date;
            } else if (id == 7) {
                id7Date = date;
            } else if (id == 14) {
                id14Date = date;
            }else if (id == 8) {
                id8Date = date;
            }
        }

        String addDayCount = settingsUtil.getValue("invoice-info-dates");

        Map<String, Object> count = JsonUtil.readValue(addDayCount, Map.class);

        Integer submittedDate = getInteger(count, "inv-submitted");
        Integer approvedDate = getInteger(count, "inv-approved");
        Integer paidDate = getInteger(count, "paid");


        // Add record for id 10 if it doesn't exist
        if (id10Date == null && id14Date != null) {
            id10Date = id14Date.plusDays(submittedDate);
            Map<String, Object> newRecord10 = new HashMap<>();
            newRecord10.put("id", 10);
            newRecord10.put("status", "Invoice Submitted");
            newRecord10.put("date", (id10Date==null) ? "":id10Date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            newRecord10.put("complete", 0);
            records.add(newRecord10);
        }

        // Add record for id 7 if it doesn't exist
        if (id7Date == null ) {
            if(id10Date!=null)
            id7Date = id10Date.plusDays(approvedDate);
            Map<String, Object> newRecord7 = new HashMap<>();
            newRecord7.put("id", 7);
            newRecord7.put("status", "Invoice Approved");
            newRecord7.put("date", (id10Date==null) ? "":id7Date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            newRecord7.put("complete", 0);
            records.add(newRecord7);
        }

        // Add record for id 8 if it doesn't exist
        if (id8Date==null) {
             id8Date = (id7Date != null) ? id7Date.plusDays(paidDate) : null;

                Map<String, Object> newRecord8 = new HashMap<>();
                newRecord8.put("id", 8);
                newRecord8.put("status", "Paid");
                newRecord8.put("date", (id10Date == null) ? "" : id8Date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                newRecord8.put("complete", 0);
                records.add(newRecord8);

        }

        return response;
    }

}
