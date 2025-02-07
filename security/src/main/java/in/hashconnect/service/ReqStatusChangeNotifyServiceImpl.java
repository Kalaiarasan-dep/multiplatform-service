package in.hashconnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.hashconnect.vo.RequestTemplateTypes;

/**
 * This is used to send email notification to partner if any changes in order id or inv id.
 */
@Service
public class ReqStatusChangeNotifyServiceImpl implements ReqStatusChangeNotifyService {

	@Autowired
	private InvoiceStatusChangeEmailService invoiceStatusChangeEmailService;
	private OrderStatusChangeEmailService orderStatusChangeEmailService;
	
	private ReqStatusChangeEmailService getEmailService(RequestTemplateTypes type) {
		switch (type.name()) {
		case "INVOICE":
			return invoiceStatusChangeEmailService;
		case "ORDER":
			return orderStatusChangeEmailService;
		}
		return null;
	}

	@Override
	public void notifyStatusChange(List<Object> ordInvIds, RequestTemplateTypes type) {
		getEmailService(type).notifyStatusChange(ordInvIds, type);
		
	}
}
