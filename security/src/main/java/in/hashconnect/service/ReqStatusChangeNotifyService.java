package in.hashconnect.service;

import java.util.List;

import in.hashconnect.vo.RequestTemplateTypes;

public interface ReqStatusChangeNotifyService {
	void notifyStatusChange(List<Object> ordInvIds, RequestTemplateTypes type); 
}
