package in.hashconnect.controller;

import in.hashconnect.controller.vo.Request;
import in.hashconnect.controller.vo.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

public interface NotificationController {

	@PostMapping("/send")
	@ResponseBody
	Response send(@RequestHeader("X_TENANT_ID") Long tenantId, @RequestBody Request request);

	@PostMapping("/karixStatus")
	@ResponseBody
	Response karix();

}
