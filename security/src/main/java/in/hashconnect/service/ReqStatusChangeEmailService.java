package in.hashconnect.service;

import java.util.List;

import in.hashconnect.vo.RequestTemplateTypes;

public interface ReqStatusChangeEmailService {
	void notifyStatusChange(List<Object> ordInvIds, RequestTemplateTypes type);
}
