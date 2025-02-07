package in.hashconnect.feign.client;

import java.util.Map;

import org.hibernate.type.descriptor.jdbc.ObjectNullResolvingJdbcType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import in.hashconnect.api.vo.Response;
import in.hashconnect.vo.TokenCreateRequest;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "omo-auth")
public interface AuthProxy {

	@PostMapping("/token/create")
	public Response<Map<String, Object>> create(@RequestBody TokenCreateRequest request);

	@PostMapping("token/changePwd")
	public Response<Map<String, Object>> changePwd(@RequestParam("userid") Integer userid, @RequestParam("cfrmPwd") String cfrmPwd,@RequestParam("errorChk") Boolean errorChk);
}
