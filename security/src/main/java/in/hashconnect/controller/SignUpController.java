package in.hashconnect.controller;

import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getString;

import java.io.IOException;
import java.util.*;

import in.hashconnect.admin.dao.PartnerDao;
import in.hashconnect.service.PartnerService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.api.ServiceFactory;
import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.service.SignUpDocUploader;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.vo.SignUpUploadVo;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/signUp")
public class SignUpController {
	private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);
	private static final String SIGNUP_BUCKET = "invoice_request_bucket";
	private static final String SIGNUP_DOCS_UPLOAD_DIR = "signup_docs_upload_dir";
	@Autowired
	private ServiceFactory factory;
	@Autowired
	private GenericDao genericDao;
	@Autowired
	private SettingsUtil settingsUtil;
	@Autowired
	private SignUpDocUploader signUpDocUploader;
	@Autowired
	private NotificationServiceFactory notificationServiceFactory;

	@Autowired
	private PartnerService partnerService;
	@Autowired
	private PartnerDao partnerDao;

	@PostMapping(value = "/create", consumes = "multipart/form-data")
	public Response<?> partnerSignUp(
			@RequestParam(value = "gstFile", required = false) MultipartFile gstFile,
			@RequestParam(value = "panCardFile", required = false) MultipartFile panCardFile,
			@RequestParam(value = "chequeFile", required = false) MultipartFile chequeFile,
			@RequestParam Map<String, Object> request
	) throws IOException {
		Response<List<Integer>> resp = Response.ok();

		List<SignUpUploadVo> uploadFiles = new ArrayList<>();

		if (gstFile != null) {
			uploadFiles.add(new SignUpUploadVo("gst", gstFile));
		}
		if (panCardFile != null) {
			uploadFiles.add(new SignUpUploadVo("pan", panCardFile));
		}
		if (chequeFile != null) {
			uploadFiles.add(new SignUpUploadVo("cheque", chequeFile));
		}

		String bucket = settingsUtil.getValue(SIGNUP_BUCKET);
		String path = settingsUtil.getValue(SIGNUP_DOCS_UPLOAD_DIR);

		uploadFiles.parallelStream().forEach(vo -> {
			try {
				signUpDocUploader.uploadFile(vo, bucket, path);
			} catch (Exception e) {
				logger.error("failed to upload file, {}", vo.getType(), e);
				vo.setError(ExceptionUtils.getStackTrace(e));
			}
		});

		boolean hasErrors = uploadFiles.stream().anyMatch(vo -> isValid(vo.getError()));
		if (hasErrors) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc(settingsUtil.getValue("partner_sign_up_failed_msg"));

			Map<String, Object> config = JsonUtil.readValue(settingsUtil.getValue("partner_sign_up_error_notification_config"), Map.class);

			Map<String, Object> params = new HashMap<>();
			uploadFiles.forEach(vo -> params.put(vo.getType() + "Error", vo.getError()));

			notificationServiceFactory.get(TYPE.EMAIL)
					.process(new Notification(getString(config, "template"), getString(config, "to"), params));
			return resp;
		}

		uploadFiles.forEach(vo -> request.put(vo.getType() + "FileName", vo.getUploadLocation()));

		uploadFiles.forEach(vo -> {
			if (vo.getType().equals("gst")) {
				request.put("gstFileName", vo.getUploadLocation());
			} else if (vo.getType().equals("pan")) {
				request.put("panFileName", vo.getUploadLocation());
			} else if (vo.getType().equals("cheque")) {
				request.put("chequeFileName", vo.getUploadLocation());
			}
		});

		if(request.get("partnerId")!=null){
			genericDao.updatePartnerDetails(request);
			resp.setStatus(STATUS.SUCCESS);
			resp.setDesc(settingsUtil.getValue("parner_sign_up_success_msg"));
			return resp;
		}

		genericDao.savePartnerDetails(request);
		Map<String, Object> params = new HashMap<>(3);
		params.put("subject", "Registration Received – OMO Portal");
		params.put("partnerName",getString(request, "ownerName"));
		notificationServiceFactory.get(Notification.TYPE.EMAIL).process(new Notification(
				settingsUtil.getValue("partner_sign_up_template"), getString(request, "ownerEmail"), 
				getString(params, "subject"), params));

		resp.setStatus(STATUS.SUCCESS);
		resp.setDesc(settingsUtil.getValue("parner_sign_up_success_msg"));
		return resp;
	}

	@PostMapping(value = "/list/{name}")
	public Response<Map<String, Object>> commonListing(@PathVariable("name") String name,
			@RequestBody Map<String, Object> params) {
		String gstNo = (String) params.get("gstNo");
		Integer stateId = (Integer) params.get("stateId");
		Map<String, Object> parameters = new HashMap<>(2);
		addParams("gstNo", gstNo, parameters);
		addParams("stateId", String.valueOf(stateId), parameters);
		Response<Map<String, Object>> list = factory.get("get").process(name, parameters);
		return list;
	}

	private void addParams(String key, String value, Map<String, Object> params) {
		if (isValid(value))
			params.put(key, value);
	}

	@GetMapping(value = "/gstTypeCheck")
	public Response<?> gstTypeCheck(@RequestParam("stateId") Integer stateId,
			@RequestParam("gstTypeId") Integer gstTypeId) {

		Response<List<Integer>> resp = Response.ok();
		Boolean exists = genericDao.getGstTypeOfState(gstTypeId, stateId);
		if (!exists) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("Under composite scheme, You are not eligible to submit an interstate Invoice.");
			return resp;
		}
		resp.setStatus(STATUS.SUCCESS);
		return resp;
	}

   // SignUp Validation API'S
	@GetMapping("/partner-validation-details")
	public Response<Map<String, Object>> getPartnerInvDetails(@RequestParam("userId") String userId) {
		Map<String, Object> map = partnerService.getPartnerValidationDtls(userId);
		Response<Map<String, Object>> resp = Response.ok();

		resp.setData(map);
		return resp;
	}

	@PostMapping("/validation-results")
	public Response<?> saveInvValidation(@RequestBody Map<String, Object> validationResp) {
		if (validationResp == null) {
			return Response.failed("Invalid input provided");
		}
		partnerService.saveSignUpValidation(validationResp);
		return Response.ok();
	}


}
