package in.hashconnect.api.dao;

import java.util.Map;

import in.hashconnect.api.vo.GetResponse;

public interface ApiDao {

	Map<String, Object> getConfig(String name);

	long create(Map<String, Object> dbInsert, Map<String, Object> params);

	void update(Map<String, Object> dbInsert, Map<String, Object> params);

	GetResponse get(Map<String, Object> dbVariant, Map<String, Object> params);
}
