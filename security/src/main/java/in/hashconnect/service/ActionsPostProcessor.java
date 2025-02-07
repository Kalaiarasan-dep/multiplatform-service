package in.hashconnect.service;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getObject;

@Service
public class ActionsPostProcessor implements CommonApiPostProcessor {
    @Autowired
    private GenericDao genericDao;

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {

        List<Integer> reasonIdsList = (List<Integer>) getObject(request, "reasonIds");
        if(reasonIdsList==null){
            return response;
        }
        List<Integer> idsList = (List<Integer>) getObject(request, "ids");
        Integer statusId = getInteger(request, "statusId");

        for (Integer orderId : idsList) {
            for (Integer reasonId : reasonIdsList) {
                genericDao.updateReasons(orderId, reasonId,statusId);
            }


        }

        return response;

    }
}
