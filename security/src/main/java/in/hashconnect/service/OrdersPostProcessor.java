package in.hashconnect.service;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdersPostProcessor implements CommonApiPreProcessor, CommonApiPostProcessor {

    @Autowired
    private GenericDao genericDao;

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if (response.getStatus() == Response.STATUS.FAILED || response.getRecords().isEmpty()) {
            return response;
        }
        GetResponse res = (GetResponse) response;

        if (request.containsKey("statusIn") && request.containsKey("programIn") && request.containsKey("monthIn") && request.containsKey("partnerIn")) {

            List<String> statusList = (List<String>) request.get("statusIn");
            List<String> programList = (List<String>) request.get("programIn");
            List<String> monthList = (List<String>) request.get("monthIn");
            List<String> partnerList = (List<String>) request.get("partnerIn");

            if (statusList.size() == 1 && programList.size() == 1 && monthList.size() == 1 && partnerList.size() >= 1) {
                for (String status : statusList) {
                    List<Map<String, Object>> actionResult = genericDao.getAction(status,"ORDER");
                    if (actionResult instanceof List) {
                        List<Map<String, Object>> resultList = (List<Map<String, Object>>) actionResult;
                        Map<String, Object> actions = new HashMap<>();
                        actions.put("actions", resultList);
                        res.setData(actions);

                    }
                }
            }

        }
        if (request.containsKey("statusIn") && request.containsKey("programIn") && request.containsKey("monthIn") && !request.containsKey("partnerIn")) {

            List<String> statusList = (List<String>) request.get("statusIn");
            List<String> programList = (List<String>) request.get("programIn");
            List<String> monthList = (List<String>) request.get("monthIn");

            if (statusList.size() == 1 && programList.size() == 1 && monthList.size() == 1) {
                for (String status : statusList) {
                    if (status.equals("13")) {
                        List<Map<String, Object>> actionResult = genericDao.getAction(status,"ORDER");
                        if (actionResult instanceof List) {
                            List<Map<String, Object>> resultList = (List<Map<String, Object>>) actionResult;
                            Map<String, Object> actions = new HashMap<>();
                            actions.put("actions", resultList);
                            res.setData(actions);

                        }
                    }
                }
            }

        }
        return response;
    }

    @Override
    public Response<?> process(Map<String, Object> data) {

        if (data != null && MapUtils.getBoolean(data, "partnerSelectAll", false)) {
            String partnerIds = genericDao.getAllPartnerIds().stream().map(String::valueOf).collect(Collectors.joining(","));
            data.put("partnerIn", partnerIds);
        }

        return Response.ok();
    }

    public String arrayListToString(ArrayList<Integer> arrayList) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            result.append(arrayList.get(i));
            if (i < arrayList.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }
}
