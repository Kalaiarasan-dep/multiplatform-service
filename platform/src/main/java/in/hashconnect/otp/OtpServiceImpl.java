package in.hashconnect.otp;

import static org.apache.commons.collections4.MapUtils.getBooleanValue;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import in.hashconnect.otp.vo.Request;
import in.hashconnect.otp.vo.Response;
import in.hashconnect.otp.vo.Response.STATUS;
import in.hashconnect.util.DateUtil;
import in.hashconnect.util.SettingsUtil;
import in.hashconnect.util.StringUtil;

public class OtpServiceImpl implements OtpService {
	private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);

	@Autowired
	private OtpDao otpDao;

	@Autowired
	private SettingsUtil settingsUtil;

	private final String OTP_PATTERN = "123456789";

	private String generate(int len) {
		char[] otp = new char[len];
		for (int i = 0; i < len; i++) {
			otp[i] = OTP_PATTERN.charAt(ThreadLocalRandom.current().nextInt(OTP_PATTERN.length()));
		}
		return new String(otp);
	}

	@Override
	public Response generate(Request request) {
		Response response = new Response();

		if (isEmpty(request.getIp()) || isEmpty(request.getTo())) {
			logger.warn("missing ip or to");

			response.setStatus(STATUS.INVALID_REQUEST);
			return response;
		}

		if (!otpDao.rateLimit(request)) {
			logger.warn("rate limit exceeded with IP: {}", request.getIp());
			response.setStatus(STATUS.RATELIMIT_EXCEEDED);
			return response;
		}

		String otp = generate(4);
		request.otp(otp);

		Integer expiry = StringUtil.convertToInt(settingsUtil.getValue("otp_expiry_seconds"));
		if (expiry == null)
			expiry = 30;

		request.setExpiryTime(convertToSqlTimestamp(DateUtil.add(new Date(), Calendar.SECOND, expiry)));
		// save request
		String refId = otpDao.save(request);

		response.setOtp(otp);
		response.setRefId(refId);
		response.setStatus(STATUS.SUCCESS);
		return response;
	}

	@Override
	public Response validate(Request request) {
		Response response = new Response();

		if (isEmpty(request.getRefId())) {
			logger.warn("missing refId");
			response.setStatus(STATUS.INVALID_REQUEST);
			return response;
		}
		Map<String, Object> result = otpDao.validate(request);

		boolean bypass = "true".equals(settingsUtil.getValue("bypass-otp-check"));
		boolean valid = bypass ? true : getBooleanValue(result, "valid");

		response.setStatus(valid ? STATUS.SUCCESS : STATUS.INVALID_OTP);
		response.setTo(getString(result, "to"));
		return response;
	}

	@Override
	public Response get(Request request) {
		Response response = new Response();

		if (isEmpty(request.getRefId())) {
			logger.warn("missing refId");
			response.setStatus(STATUS.INVALID_REQUEST);
			return response;
		}

		Map<String, Object> result = otpDao.getByRefId(request);
		String otp = getString(result, "otp");
		String to = getString(result, "to");

		if (MapUtils.isEmpty(result) || isEmpty(otp)) {
			logger.warn("invalid refId");
			response.setStatus(STATUS.INVALID_REQUEST);
			return response;
		}

		response.setOtp(otp);
		response.setTo(to);
		response.setStatus(STATUS.SUCCESS);
		return response;
	}

	private java.sql.Timestamp convertToSqlTimestamp(Date dt) {
		return new Timestamp(dt.getTime());
	}
}
