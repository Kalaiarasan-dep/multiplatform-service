package in.hashconnect.service.impl;


import in.hashconnect.common.vo.KarixStatus;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.util.HttpUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractNotificationService {

	private GenericDao genericDao;
	private VelocityEngine velocityEngine;
	private HttpUtil httpUtil;

	protected Map<String, Object> getTemplateDetailsByKey(String key) {
		return genericDao.getTemplateDetailsByKey(key);
	}

	protected Long persistNotification(Map<String, Object> notificationParams, Map<String, Object> params, Long clientId) {

		return genericDao.insertNotification(notificationParams, params, clientId);
	}

	protected void updateStatus(Long id, String status, String apiResponse, String refId) {
		genericDao.updateNotification(id, status, apiResponse, refId);
	}

	protected String getBody(String template, Map<String, Object> params) {
		VelocityContext context = new VelocityContext(params);
		StringWriter writer = new StringWriter();

		synchronized (template.intern()) {
			velocityEngine.mergeTemplate(template, "UTF-8", context, writer);
		}

		return writer.toString();
	}

	public void setGenericDao(GenericDao genericDao) {
		this.genericDao = genericDao;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	protected String getTemplateDetailsByRefId(String refid) {
		return genericDao.getTemplateDetailsByRefId(refid);
	}

	public List<KarixStatus> getOpearatorStsPendingRecords(String str) {
		return genericDao.getOpearatorStsPendingRecords(str);
	}

	public void updateNotificationStatus(String refId, String operatorSts) {
		genericDao.updateNotificationStatus(refId, operatorSts);
	}

	public void updateStatus() {
	}


	public HttpUtil getHttpUtil() {
		return httpUtil;
	}

	public void setHttpUtil(HttpUtil httpUtil) {
		this.httpUtil = httpUtil;
	}

	protected Map<String,Object> buildNotificationParams(Integer templateId,String type
			, String to, String from, String cc, String subject){
		Map<String,Object> notificationParams = new HashMap<>();
		notificationParams.put("template",templateId);
		notificationParams.put("type",type);
		notificationParams.put("to",to);
		notificationParams.put("from",from);
		notificationParams.put("cc",cc);
		notificationParams.put("subject",subject);
		return notificationParams;
	}
}
