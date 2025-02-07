package in.hashconnect.controller;

import static in.hashconnect.util.StringUtil.concate;
import static in.hashconnect.util.StringUtil.isValid;
import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import in.hashconnect.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.hashconnect.api.ServiceFactory;
import in.hashconnect.api.vo.GetResponse;
import in.hashconnect.api.vo.Response;
import in.hashconnect.api.vo.Response.STATUS;
import in.hashconnect.dao.GenericDao;
import in.hashconnect.feign.client.AuthProxy;
import in.hashconnect.notification.service.impl.NotificationServiceFactory;
import in.hashconnect.notification.service.vo.Notification;
import in.hashconnect.notification.service.vo.Notification.TYPE;
import in.hashconnect.storage.StorageService;
import in.hashconnect.service.InvoiceExportService;
import in.hashconnect.service.InvoiceOrdersExportService;
import in.hashconnect.service.OrdersExportService;
import in.hashconnect.service.PartnerService;
import in.hashconnect.service.ReqStatusChangeNotifyService;
import in.hashconnect.service.StatementAccountService;
import in.hashconnect.storage.vo.FileContent;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.JsonUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;
import in.hashconnect.vo.PartnerConstants;
import in.hashconnect.vo.PartnerResponse;
import in.hashconnect.vo.RequestTemplateTypes;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/admin")
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	private static final String EXCEL_EXT = ".xlsx";
	private static final String EXCEL_PREFIX = "attachment; filename=All_orders_";

	private static final String INVOICE_EXCEL_PREFIX = "attachment; filename=All_Invoices_";
	private static final String EXCEL_CONTENT_TYPE = "application/octet-stream";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String INVOICE_REQUEST_BUCKET = "invoice_request_bucket";
	private static final String INVOICE_REQUEST_UPLOAD_DIR = "invoice_request_upload_dir";

	private static final String PARTNERS_MASTER_EXCEL_PREFIX = "attachment; filename=Partners_Export_";
	@Autowired
	private GenericDao genericDao;
	@Autowired
	private ServiceFactory factory;
	@Autowired
	private SettingsUtil settingsUtil;
	@Autowired
	private StorageService s3Client;
	@Autowired
	private PartnerService partnerService;
	@Autowired
	private ReqStatusChangeNotifyService reqStatusChangeNotifyService;
	@Autowired
	private NotificationServiceFactory notificationServiceFactory;
	@Autowired
	private StatementAccountService soa;

	@Autowired
	private AuthProxy authProxy;

	@GetMapping("/now")
	public String getTime() {
		return genericDao.getNow();
	}

	@PostMapping("/orders-export")
	public void ordersExport(HttpServletResponse response, @RequestHeader("USER_ID") String userId,
			@RequestBody Map<String, Object> params, @RequestHeader("ROLE") String role) throws Exception {

		response.setContentType(EXCEL_CONTENT_TYPE);
		String fileName = concate(EXCEL_PREFIX, DateUtil.format("yyyy-MM-dd_HH_mm_ss", new Date()), EXCEL_EXT);
		response.setHeader(CONTENT_DISPOSITION, fileName);

		List<Integer> statusIn = (List<Integer>) params.get("statusIn");
		List<Integer> programIn = (List<Integer>) params.get("programIn");
		List<Integer> partnerIn = (List<Integer>) params.get("partnerIn");
		List<Integer> batchIn = (List<Integer>) params.get("batchIn");
		List<Integer> monthIn = (List<Integer>) params.get("monthIn");
		String stDtGrEq = (String) params.get("stDtGrEq");
		String enDtLsEq = (String) params.get("enDtLsEq");
        Boolean partnerSelectAll= (Boolean) params.get("partnerSelectAll");
		Map<String, Object> parameters = new HashMap<>(2);
		addParams("userid", userId, parameters);
		addParams("role", role, parameters);

		if (statusIn != null)
			parameters.put("statusIn", arrayListToString((ArrayList<Integer>) statusIn));
		if (programIn != null)
			parameters.put("programIn", arrayListToString((ArrayList<Integer>) programIn));
		if (partnerIn != null)
			parameters.put("partnerIn", arrayListToString((ArrayList<Integer>) partnerIn));
		if (batchIn != null)
			parameters.put("batchIn", arrayListToString((ArrayList<Integer>) batchIn));
		if (monthIn != null)
			parameters.put("monthIn", arrayListToString((ArrayList<Integer>) monthIn));
		if (enDtLsEq != null)
			parameters.put("enDtLsEq", enDtLsEq);
		if (stDtGrEq != null)
			parameters.put("stDtGrEq", stDtGrEq);
		if (partnerSelectAll != null)
			parameters.put("partnerSelectAll", partnerSelectAll);


		Response<?> queryResponse = factory.get("get").process("orders", parameters);
		GetResponse getResponse = (GetResponse) queryResponse;
		List<Map<String, Object>> list = getResponse.getRecords();

		// write to excel
		OrdersExportService excelExporter = new OrdersExportService(list, response);
		excelExporter.writeRows();
	}

	@PostMapping("/invoice-export")
	public void invoiceExport(HttpServletResponse response, @RequestHeader("USER_ID") String userId,
							  @RequestBody Map<String, Object> params, @RequestHeader("ROLE") String role) throws Exception {
		// updating headers
		response.setContentType(EXCEL_CONTENT_TYPE);
		String fileName = concate(INVOICE_EXCEL_PREFIX, DateUtil.format("yyyy-MM-dd_HH_mm_ss", new Date()), EXCEL_EXT);
		response.setHeader(CONTENT_DISPOSITION, fileName);
		List<Integer> statusIn = (List<Integer>) params.get("statusIn");
		List<Integer> programIn = (List<Integer>) params.get("programIn");
		List<Integer> partnerIn = (List<Integer>) params.get("partnerIn");
		List<Integer> batchIn = (List<Integer>) params.get("batchIn");
		List<Integer> monthIn = (List<Integer>) params.get("monthIn");
		List<Integer> gstIn = (List<Integer>) params.get("gstIn");
		Boolean partnerSelectAll= (Boolean) params.get("partnerSelectAll");
		Map<String, Object> parameters = new HashMap<>(2);
		parameters.put("userid", userId);
		parameters.put("role", role);

		if (statusIn != null)
			parameters.put("statusIn", arrayListToString((ArrayList<Integer>) statusIn));
		if (programIn != null)
			parameters.put("programIn", arrayListToString((ArrayList<Integer>) programIn));
		if (partnerIn != null)
			parameters.put("partnerIn", arrayListToString((ArrayList<Integer>) partnerIn));
		if (batchIn != null)
			parameters.put("batchIn", arrayListToString((ArrayList<Integer>) batchIn));
		if (monthIn != null)
			parameters.put("monthIn", arrayListToString((ArrayList<Integer>) monthIn));
		if (gstIn != null)
			parameters.put("gstIn", arrayListToString((ArrayList<Integer>) gstIn));
		if (partnerSelectAll != null)
			parameters.put("partnerSelectAll", partnerSelectAll);

		Response<?> queryResponse = factory.get("get").process("invoice-request", parameters);
		GetResponse getResponse = (GetResponse) queryResponse;

		List<Map<String, Object>> list = getResponse.getRecords();

		// write to excel
		InvoiceExportService excelExporter = new InvoiceExportService(list, response);
		excelExporter.writeRows();
	}

	public String arrayListToString(ArrayList<Integer> arrayList) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < arrayList.size(); i++) {
			result.append(arrayList.get(i));
			if (i < arrayList.size() - 1) {
				result.append(",");
			}
		}
		return result.toString();
	}

	@GetMapping("/inv-orders-export")
	public void exportToExcel(HttpServletResponse response, @RequestHeader("USER_ID") String userId,
			@RequestHeader("ROLE") String role, @RequestParam("id") String id) throws Exception {
		// updating headers
		response.setContentType(EXCEL_CONTENT_TYPE);

		Map<String, Object> invDetails = genericDao.getInvDetails(id);
		String program = (String) invDetails.get("program");
		String month = (String) invDetails.get("month");
		String EXCEL_PREFIX = "attachment; filename=" + concate(program, "_", month, "_", id);
		String fileName = concate(EXCEL_PREFIX, EXCEL_EXT);
		response.setHeader(CONTENT_DISPOSITION, fileName);

		Map<String, Object> params = new HashMap<>(2);
		addParams("userid", userId, params);
		addParams("role", role, params);
		addParams("id", id, params);

		Response<?> queryResponse = factory.get("get").process("invoice-request-orders", params);
		GetResponse getResponse = (GetResponse) queryResponse;

		List<Map<String, Object>> list = getResponse.getRecords();

		// write to excel
		InvoiceOrdersExportService excelExporter = new InvoiceOrdersExportService(list, response);
		excelExporter.writeRows();
	}

	private void addParams(String key, String value, Map<String, Object> params) {
		if (isValid(value))
			params.put(key, value);
	}

	@GetMapping("/hc-details")
	public Response<Map<String, String>> getPartnerConfirmMessages(@RequestParam("invid") String invId) {
		Response<Map<String, String>> r = Response.ok();
		Map<String, Object> bankMap =  partnerService.getPartnerGSTAndBankDetailsByInvNo(invId);
		String bankStr = StringUtil.concate("<div class=\"details\">  <h5>Confirm Bank Account</h5>\n <p>",
				bankMap.get("bank_name"), "<br>", bankMap.get("account_number"), "</p></div>");
		String gstStr = StringUtil.concate("<div class=\"details\">  <h5>Confirm GST</h5>\n <p>",
				bankMap.get("gst_number"), "</p></div>");
		Map<String, String> map = JsonUtil.readValue(settingsUtil.getValue(PartnerConstants.HC_DETAILS), Map.class);
		map.put("bank", bankStr);
		map.put("gst", gstStr);
		r.setData(map);
		return r;
	}

	@PostMapping(value = "/inv-submit", consumes = "multipart/form-data")
	public Response<?> invoiceSubmission(@RequestHeader("USER_ID") Integer userId,
			@RequestParam("file") MultipartFile file, @RequestParam Map<String, Object> request) throws IOException {
		// first validate input fields
		Response<PartnerResponse> response = Response.ok();
		PartnerResponse validationResp = partnerService.validatePartner(request);
		if (validationResp.getErrors().size() > 0) {
			response.setStatus(STATUS.FAILED);
			response.setData(validationResp);
			return response;
		}

		Integer invRequestId = getInteger(request, "invReqId");
				
		// upload file to s3
		String bucket = settingsUtil.getValue(INVOICE_REQUEST_BUCKET);
		String path = settingsUtil.getValue(INVOICE_REQUEST_UPLOAD_DIR);
		String fileName = concate("inv-", invRequestId, "-", System.currentTimeMillis(), ".",
				FilenameUtils.getExtension(file.getOriginalFilename()));

		byte[] data = IOUtils.toByteArray(file.getInputStream());

		FileContent fc = new FileContent();
		fc.setData(data);
		fc.setFolder(path);
		fc.setName(fileName);
		fc.setBucket(bucket);
		fc.setContentType(file.getContentType());
		s3Client.put(fc);

		fileName = new File(path, fileName).getAbsolutePath();
		String role = partnerService.getUserRoleByUserId(userId);
		request.put("fileName", fileName);
		request.put("userid", userId);
		request.put("invoiceAmt", request.get("invoiceAmt"));
		request.put("invReqId", invRequestId);
		request.put("invoiceNo", request.get("invoiceNo"));
		request.put("invoiceDt", request.get("invoiceDt"));
		request.put("igst", request.get("igst"));
		request.put("cgst", request.get("cgst"));
		request.put("sgst", request.get("sgst"));
		request.put("contentType", fc.getContentType());
		request.put("userRole", role);
		partnerService.submitInvoice(request);
		
		if (sendEmail(userId, role)) {
			reqStatusChangeNotifyService.notifyStatusChange(
					Arrays.asList(invRequestId), RequestTemplateTypes.INVOICE);
		}
		return Response.ok();
	}
	
	/**
	 * Used to decide send email notification based on user role.
	 * Here we are not sending email for admin and sending it for partner. 
	 * we can add user role who doesn't need email in settings table user_role_not_for_email_trigger
	 * @param userId
	 * @param role
	 * @return
	 */
	private boolean sendEmail(Integer userId, String role) {
		String userRoleNotForEmailTrigger = settingsUtil.getValue("user_role_not_for_email_trigger");
		if (!StringUtil.isValid(userRoleNotForEmailTrigger)) {
			return true;
		}
		List<String> userRoleListNotForEmailTrigger = Collections.emptyList();
		userRoleListNotForEmailTrigger = Arrays.asList(userRoleNotForEmailTrigger.split(","));
		if (userRoleListNotForEmailTrigger.contains(role)) {
			return false;
		}
		return true;
	}

	@PostMapping("/inv-request")
	public Response<?> invoiceRequest(@RequestHeader("USER_ID") String userId,
			@RequestBody Map<String, List<Integer>> requestBody) {

		List<Integer> partnerIds = requestBody.get("partnerId");
		List<Integer> orderIds = requestBody.get("ids");

		// validate request
		if (CollectionUtils.isEmpty(partnerIds) || CollectionUtils.isEmpty(orderIds)) {
			return Response.failed("invalid request, missing partnerIds/orderIds");
		}

		partnerIds.parallelStream().forEach(pid -> {
			// find partner specific order ids
			List<Integer> partnerOrderIds = genericDao.getOrderIds(userId, pid, orderIds);
			if (CollectionUtils.isNotEmpty(partnerOrderIds)) {
				Map<String, Object> params = new HashMap<>(2);
				params.put("userid", userId);
				params.put("partnerId", pid);
				params.put("ids", partnerOrderIds);

				factory.get("create").process("invoice-request", params);
			}
		});
		return Response.ok();
	}

	@PostMapping("/inv-req-resend")
	public Response<?> sendEmail(@RequestBody Map<String, List<Object>> invIds) {
		List<List<Object>> list = invIds.values().stream().collect(Collectors.toList());
		if (list.isEmpty() || list.get(0).isEmpty()) {
			Response<List<Integer>> resp = Response.ok();
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("No data provided");
			return resp;
		}

		reqStatusChangeNotifyService.notifyStatusChange(list.get(0), RequestTemplateTypes.INVOICE);
		return Response.ok();
	}

	@PostMapping("/bulk-action")
	public Response<?> bulkAction(HttpServletResponse response, @RequestHeader("USER_ID") String userId,
			@RequestParam("type") String type, @RequestBody Map<String, Object> params,
			@RequestHeader("ROLE") String role) throws Exception {
		Response<List<Integer>> resp = Response.ok();

		Integer statusIn = (Integer) params.get("statusIn");
		Integer programIn = (Integer) params.get("programIn");
		Boolean partnerSelectAll= (Boolean) params.get("partnerSelectAll");
		List<Integer> partnerIn;
		if(!partnerSelectAll){
			partnerIn = (List<Integer>) params.get("partnerIn");
		}else{
			partnerIn=genericDao.getAllPartnerIds();
		}

		Integer batchIn = (Integer) params.get("batchIn");
		String stDtGrEq = (String) params.get("stDtGrEq");
		String enDtLsEq = (String) params.get("enDtLsEq");

		List<Integer> ids = genericDao.getBulkActionIds(statusIn, programIn, batchIn, partnerIn, userId, stDtGrEq,
				enDtLsEq);
		if (ids == null || ids.isEmpty()) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("Orders not present for Bulk Action");
			return resp;
		}

		Map<String, Object> parameters = new HashMap<>(2);
		parameters.put("userid", userId);
		parameters.put("ids", ids);

		if (type.equalsIgnoreCase("approve")) {
			factory.get("update").process("approve", parameters);
			resp.setStatus(STATUS.SUCCESS);
			resp.setDesc("Status Updated Successfully");
			return resp;
		}

		if (type.equalsIgnoreCase("reject")) {
			parameters.put("statusId", null);
			parameters.put("reasonIds", null);
			factory.get("update").process("reject", parameters);
			resp.setStatus(STATUS.SUCCESS);
			resp.setDesc("Status Updated Successfully");
			return resp;
		}

		if (type.equalsIgnoreCase("onhold")) {
			parameters.put("statusId", null);
			parameters.put("reasonIds", null);
			factory.get("update").process("onhold", parameters);
			resp.setStatus(STATUS.SUCCESS);
			resp.setDesc("Status Updated Successfully");
			return resp;
		}

		if (type.equalsIgnoreCase("send_invoice_request")) {
			final Map<String, Object> shared = new HashMap<>();

			List<Integer> finalPartnerIn = partnerIn;
			new Thread(()->{
					Map<String, List<Integer>> requestBody = new HashMap<>();
					requestBody.put("partnerId", finalPartnerIn);
					requestBody.put("ids", ids);
					invoiceRequest(userId, requestBody);

					shared.put("completed", true);

					if (getBooleanValue(shared, "responded")) {
						// send notification
						Map<String, Object> data = new HashMap<>();
						// find loggedIn user details
						data.putAll(genericDao.getUserDetailsById(Integer.parseInt(userId)));

						notificationServiceFactory.get(TYPE.EMAIL).process(
								new Notification(settingsUtil.getValue("bulk_invoice_request_delayed_template"),
										getString(data, "email"), data));
					}

					// notify the waiting person
					synchronized (shared) {
						shared.notify();
					}

			}).start();

			// pls wait while process is on
			synchronized (shared) {
				shared.wait(1000 * settingsUtil.getIntValue("bulk_invoice_request_wait_time_in_sec"));
			}

			shared.put("responded", true);

			// after wait is complete, check if process is completed
			if (!getBooleanValue(shared, "completed")) {
				resp.setStatus(STATUS.DELAYED);
				resp.setDesc(settingsUtil.getValue("bulk_invoice_request_longer_msg"));
				return resp;
			}
		}

		resp.setStatus(STATUS.SUCCESS);
		resp.setDesc("Status Updated Successfully");
		return resp;
	}

	
	@GetMapping("/inv-partner-dtls")
	public Response<Map<String, Object>> getPartnerInvDetails(@RequestParam("invid") String invId) {
		Map<String, Object> map = partnerService.getPartnerInvoiceDtls(invId);
		Response<Map<String, Object>> resp = Response.ok();
		resp.setData(map);
		return resp;
	}
	
	@PostMapping("inv-validation")
	public Response<?> saveInvValidation(@RequestBody Map<String, Object> validationResp) {
		partnerService.saveInvValidation(validationResp);
		reqStatusChangeNotifyService.notifyStatusChange(Arrays.asList(validationResp.get("invId")), RequestTemplateTypes.INVOICE);
		return Response.ok();
		
	}

	@PostMapping(value = "/partner-sign-up", consumes = "multipart/form-data")
	public Response<?> partnerSignUp(@RequestParam("gstFile") MultipartFile gstFile, @RequestParam("panCardFile") MultipartFile panCardFile, @RequestParam("chequeFile") MultipartFile chequeFile, @RequestParam Map<String, Object> request) throws IOException {
		// first validate input fields
		Response<List<Integer>> resp = Response.ok();


		resp.setStatus(STATUS.SUCCESS);
		resp.setDesc("Sign up Successful");
		return resp;
	}
	
	@GetMapping("/soa")
	public void generateSOA(HttpServletResponse response, @RequestHeader("USER_ID") String userId) {
		soa.generateSOAReport(userId, response);
	}

	@PostMapping("/changePwd")
	public Response changePwd(@RequestHeader ("USER_ID") Integer userId,@RequestHeader ("ROLE") String role,
						  @RequestBody Map<String, Object> params){
		Response<List<Integer>> resp = Response.ok();
		String crtPwd= (String) params.get("currentPwd");
		Boolean errorChk =  (Boolean) params.get("errorChk") ;

		if(crtPwd!=null && !crtPwd.isEmpty()){
			Boolean exists=genericDao.checkCurrentPwd((String) params.get("currentPwd"),userId);
			if (!exists) {
				resp.setStatus(STATUS.FAILED);
				resp.setDesc("Incorrect Password");
				return resp;
			}
			resp.setStatus(STATUS.SUCCESS);
			return resp;
		}
		String newPwd=(String) params.get("newPwd");
		String cfrmPwd=(String) params.get("cfrmPwd");

		if (errorChk && newPwd != null && cfrmPwd != null && !newPwd.equals(cfrmPwd)) {
			resp.setStatus(STATUS.FAILED);
			resp.setDesc("Mismatch in Password, Re-Type Password.");
			return resp;
		}
		params.put("userId",userId);
		Response changePwd=authProxy.changePwd(userId,cfrmPwd,errorChk);
		return changePwd;

	}
	
	@GetMapping("/partner-banner")
	public Response<?> getPartnerBannerDtls(@RequestHeader("USER_ID") String userId) {
		Map<String, String> map =  partnerService.getPartnerBannerDtls(userId);
		Response<Map<String, String>> resp = Response.ok();
		resp.setStatus(STATUS.SUCCESS);
		resp.setData(map);
		
		return resp;
		
	}
	@PostMapping("/partners-master-export")
	public void partnersMasterExport(HttpServletResponse response, @RequestHeader("USER_ID") String userId,
							  @RequestBody Map<String, Object> params, @RequestHeader("ROLE") String role) throws Exception {
		// updating headers
		response.setContentType(EXCEL_CONTENT_TYPE);
		String fileName = concate(PARTNERS_MASTER_EXCEL_PREFIX, DateUtil.format("yyyy-MM-dd_HH_mm_ss", new Date()), EXCEL_EXT);
		response.setHeader(CONTENT_DISPOSITION, fileName);
		List<Integer> statusIn = (List<Integer>) params.get("statusIn");
		List<Integer> cityIn = (List<Integer>) params.get("cityIn");
		List<Integer> stateIn = (List<Integer>) params.get("stateIn");
		List<Integer> partnerIn = (List<Integer>) params.get("partnerIn");
		String stDtGrEq = (String) params.get("stDtGrEq");
		String enDtLsEq = (String) params.get("enDtLsEq");

		Map<String, Object> parameters = new HashMap<>(2);
		parameters.put("userid", userId);
		parameters.put("role", role);

		if (statusIn != null)
			parameters.put("statusIn", arrayListToString((ArrayList<Integer>) statusIn));
		if (cityIn != null)
			parameters.put("cityIn", arrayListToString((ArrayList<Integer>) cityIn));
		if (stateIn != null)
			parameters.put("stateIn", arrayListToString((ArrayList<Integer>) stateIn));
		if (partnerIn != null)
			parameters.put("partnerIn", arrayListToString((ArrayList<Integer>) partnerIn));
		if (enDtLsEq != null)
			parameters.put("enDtLsEq", enDtLsEq);
		if (stDtGrEq != null)
			parameters.put("stDtGrEq", stDtGrEq);


		Response<?> queryResponse = factory.get("get").process("partners-master-export", parameters);
		GetResponse getResponse = (GetResponse) queryResponse;

		List<Map<String, Object>> list = getResponse.getRecords();

		// write to excel
		PartnersMasterExportService excelExporter = new PartnersMasterExportService(list, response);
		excelExporter.writeRows();
	}

	@PostMapping("/partner-status-update")
	public Response<?> updatePartnerStatus(@RequestParam("USER_ID") Long partnerUserId,@RequestParam("STATUS_ID") Integer statusId,@RequestParam("PARTNER_ID") Long partnerId,@RequestParam("type") String type) {
		partnerService.savePartnerStatus(partnerUserId,statusId,partnerId,type);
		Response<Map<String, String>> resp = Response.ok();
		resp.setStatus(STATUS.SUCCESS);
		return resp;

	}

}
