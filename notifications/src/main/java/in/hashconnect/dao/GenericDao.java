package in.hashconnect.dao;

import in.hashconnect.common.vo.KarixStatus;
import in.hashconnect.controller.vo.OtpEntry;

import java.util.List;
import java.util.Map;

public interface GenericDao {

	Map<String, Object> getTemplateDetailsByKey(String key);

	Long insertNotification(Map<String, Object> notificationParams, Map<String, Object> params, Long clientId);

	void updateNotification(Long id, String status, String apiResponse,String refId);

	String getTemplateDetailsByRefId(String refid);

	List<KarixStatus> getOpearatorStsPendingRecords(String str);

	void updateNotificationStatus(String refId,String operatorSts);

	public Long addOtp(OtpEntry otpEntry);

	public int getOtpByIntervalAndIp(String ip, Integer interval);

	public Boolean validateOtp(String userId,String otp);
}
