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
public class BulkActionUpdatedStatusPostProcessor implements CommonApiPostProcessor {

    @Autowired
    private GenericDao genericDao;

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if (response.getStatus() == Response.STATUS.FAILED  || response.getRecords().isEmpty()) {
            return response;
        }
        GetResponse res = (GetResponse) response;

        res.getRecords().stream().forEach(r-> {
            // parsing updated Status
            String strStatus = getString(r, "updatedStatus");
            List<Map<String, Object>> status = JsonUtil.readValue(strStatus, new TypeReference<List<Map<String, Object>>>() {
            });
            r.put("updatedStatus", status);
        });

        return response;
    }

}
