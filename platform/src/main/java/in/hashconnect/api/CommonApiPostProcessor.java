package in.hashconnect.api;

import java.util.Map;

import in.hashconnect.api.vo.Response;

public interface CommonApiPostProcessor {
	Response<?> process(Response<?> response, Map<String, Object> data);
}
