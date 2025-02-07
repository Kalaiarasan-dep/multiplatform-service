package in.hashconnect.api;

import static in.hashconnect.util.StringUtil.isValid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.hashconnect.api.vo.Response;

@ConditionalOnProperty(value = "common.controller.enabled", havingValue = "true", matchIfMissing = false)
@RestController
@RequestMapping("${common.controller.prefix}")
public class CommonController {

	@Autowired
	private ServiceFactory factory;

	@GetMapping("/{name}")
	public Response<Map<String, Object>> get(@RequestHeader(name = "USER_ID", required = false) String userId,
			@RequestHeader(name = "ROLE", required = false) String role, @PathVariable("name") String name,
			@RequestParam Map<String, Object> params) {
		addParams("userid", userId, params);
		addParams("role", role, params);
		return factory.get("get").process(name, params);
	}

	@PostMapping("/{name}")
	public Response<Map<String, Object>> create(@RequestHeader(name = "USER_ID", required = false) String userId,
			@RequestHeader(name = "ROLE", required = false) String role, @PathVariable("name") String name,
			@RequestBody Map<String, Object> params) {
		addParams("userid", userId, params);
		return factory.get("create").process(name, params);
	}

	@PutMapping("/{name}")
	public Response<Map<String, Object>> update(@RequestHeader(name = "USER_ID", required = false) String userId,
			@RequestHeader(name = "ROLE", required = false) String role, @PathVariable("name") String name,
			@RequestBody Map<String, Object> params) {
		addParams("userid", userId, params);
		return factory.get("update").process(name, params);
	}

	private void addParams(String key, String value, Map<String, Object> params) {
		if (isValid(value))
			params.put(key, value);
	}
}
