package in.hashconnect.common.service;

import in.hashconnect.common.beans.Leads;

public interface LeadService {

	void saveOrUpdate(Leads lead);
	
	void sendNotificationCallLater(Leads lead);
	
}
