package in.hashconnect.notification.dao;

import java.util.List;
import java.util.Map;

public interface NotificationDao {
	Map<String, Object> getTemplateDetailsByKey(String key);

	Long insertNotification(String template, String type, String to, String from, String cc, String subject,
			Map<String, Object> params);

	void updateNotification(Long id, String status, String apiResponse, String refId);

	List<Map<String, Object>> getPendingNotification();

	void updateOperatorStatus(Long id, String string);
}
