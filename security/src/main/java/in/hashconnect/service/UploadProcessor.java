package in.hashconnect.service;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.api.vo.Response;
import in.hashconnect.vo.BulkImportValidateVo;
import in.hashconnect.vo.BulkImportVo;

public interface UploadProcessor {
	Response<BulkImportVo> bulkImport(MultipartFile file, String userId, String programId,String batchId);

	Response<BulkImportValidateVo> validate(Map<String, Object> request);

	Response<?> process(@RequestBody Map<String, Object> request);
}
