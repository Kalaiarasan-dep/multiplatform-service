package in.hashconnect.api;

import java.util.Map;

import in.hashconnect.api.vo.Response;

public interface CommonService {

	Response<Map<String, Object>> process(String name, Map<String, Object> params);
}
