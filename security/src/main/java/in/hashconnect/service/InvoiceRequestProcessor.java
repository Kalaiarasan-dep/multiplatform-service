package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.getObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import in.hashconnect.api.CommonApiPostProcessor;
import in.hashconnect.api.CommonApiPreProcessor;
import in.hashconnect.api.vo.CreateResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.vo.RequestTemplateTypes;


@Service
public class InvoiceRequestProcessor implements CommonApiPreProcessor, CommonApiPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceRequestProcessor.class);
    @Autowired
    private GenericDao genericDao;
    @Autowired
    private TaxCalculator taxCalculator;
    @Autowired
	private ReqStatusChangeNotifyService reqStatusChangeNotifyService;
    //Post-processor

    @Override
    public Response<?> process(Response<?> response, Map<String, Object> request) {
        if (response.getStatus() == Response.STATUS.FAILED) {
            return response;
        }

        List<Integer> idsList = (List<Integer>) getObject(request, "ids");

        CreateResponse res = (CreateResponse) response;
        Long invoiceRequestId = (Long) res.getData().get("id");

        for (Integer id : idsList) {
            genericDao.updateInvoiceId(invoiceRequestId, id);
        }
        reqStatusChangeNotifyService.notifyStatusChange(Arrays.asList(invoiceRequestId), RequestTemplateTypes.INVOICE);
        return response;
    }

    //Pre-processor
        @Override
        public Response<?> process(Map<String, Object> data) {
            List<Integer> ids = (List<Integer>) data.get("ids");
            BigDecimal totalamount=genericDao.getTotalAmount(ids);

            if (CollectionUtils.isEmpty(Collections.singleton(totalamount)))
                return Response.failed("No amount present");
            data.put("totalAmount", totalamount);
            Integer partnerId=(Integer)data.get("partnerId");
            String taxType= genericDao.getTaxType(partnerId);
            Map<String, BigDecimal> taxResults =  taxCalculator.calculate(taxType, String.valueOf(totalamount));
            data.putAll(taxResults);
            return Response.ok();
        }

}
