package in.hashconnect.service;

import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getString;

@Service
public class ReasonsPreProcessor  implements CommonApiPreProcessor {
    @Autowired
    private GenericDao genericDao;
    @Override
    public Response<?> process(Map<String, Object> data) {

        List<Integer> ids = (List<Integer>) data.get("ids");
        Integer partnerId = (Integer) data.get("partnerId");
        Integer programId= genericDao.getProgramId(ids);
        Map<String, Object> partnerDetails=genericDao.getPartnerDetails(partnerId);
        String merchantId = null;
        String contractId = null;
        Map<String, Object> partnerInfo = new HashMap<>();
        if(programId==1) {
            merchantId = getString(partnerDetails, "merchant_id");
            partnerInfo.put("merchantId", merchantId);
        }
        if(programId==2 || programId==3) {
            contractId = getString(partnerDetails, "contract_id");
            partnerInfo.put("contractId", contractId);
        }

        data.putAll(partnerInfo);
        return Response.ok();
    }
}
