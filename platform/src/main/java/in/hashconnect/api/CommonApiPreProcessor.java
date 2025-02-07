package in.hashconnect.api;

import java.util.Map;

import in.hashconnect.api.vo.Response;

public interface CommonApiPreProcessor {
	Response<?> process(Map<String, Object> data);
}
