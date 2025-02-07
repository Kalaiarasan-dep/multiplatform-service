package in.hashconnect.service;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PartnersMasterPostProcessor implements CommonApiPostProcessor {

    @Autowired
    private GenericDao genericDao;

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if (response.getStatus() == Response.STATUS.FAILED ) {
            return response;
        }
        GetResponse res = (GetResponse) response;
            res.setTotalRecords(genericDao.getPartnerMasterCount(request));

        return response;
    }



}
