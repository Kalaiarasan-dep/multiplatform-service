package in.hashconnect.controller;

import static org.apache.commons.collections4.MapUtils.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.api.vo.Response;
import in.hashconnect.service.UploadProcessorFactory;
import in.hashconnect.vo.BulkImportValidateVo;
import in.hashconnect.vo.BulkImportVo;

@RestController
@RequestMapping("/admin/upload")
public class UploadController {
	@Autowired
	private UploadProcessorFactory leadProcessorFactory;

	@PostMapping(value = "/import", consumes = "multipart/form-data")
	public Response<BulkImportVo> bulkImport(@RequestHeader("USER_ID") String userId,
			@RequestParam("file") MultipartFile file, @RequestParam("type") String type,
			@RequestParam(name = "programId", required = false) String programId,@RequestParam(name = "batchId", required = false) String batchId) {
		return leadProcessorFactory.get(type).bulkImport(file, userId, programId,batchId);
	}

	@PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
	public Response<BulkImportValidateVo> validate(@RequestBody Map<String, Object> request) {
		String type = getString(request, "type");
		return leadProcessorFactory.get(type).validate(request);
	}

	@PostMapping(value = "/process", consumes = "application/json", produces = "application/json")
	public Response<?> process(@RequestBody Map<String, Object> request) {
		String type = getString(request, "type");
		return leadProcessorFactory.get(type).process(request);
	}
}
