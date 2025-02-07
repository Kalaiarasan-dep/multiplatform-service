package in.hashconnect.service;

import com.fasterxml.jackson.core.type.TypeReference;
import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getString;

@Service
public class DashboardPostProcessor implements CommonApiPostProcessor {
    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if(response.getStatus() == Response.STATUS.FAILED) {
            return response;
        }

        // format the data
        GetResponse res = (GetResponse) response;
        res.getRecords().stream().forEach(r -> {
            // parsing data
            String strData = getString(r, "data");
            Map<String, Object> data = JsonUtil.readValue(strData, new TypeReference<Map<String, Object>>() {});
            r.put("data", data);
        });
        return response;
    }

}
