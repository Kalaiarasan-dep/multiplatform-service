package in.hashconnect.advice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import in.hashconnect.api.exception.MissingTokenException;

@ControllerAdvice
public class ExceptionAdvice {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

	@Autowired
	private ExceptionDao exceptionDao;

	@ExceptionHandler(value = Throwable.class)
	@ResponseBody
	@ResponseStatus()
	public Map<String, Object> handler(Throwable ex) {
		logger.error("something went wrong!", ex);

		Map<String, Object> error = new HashMap<>();
		error.put("error", ExceptionUtils.getStackTrace(ex));
		log(error);

		Map<String, Object> response = new HashMap<>(2);
		response.put("status", "FAILED");
		response.put("desc", "technical error, please retry later!");
		return response;
	}

	@ExceptionHandler(value = MissingTokenException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public void handlerMissingToken(Throwable ex) {
	}

	private void log(Map<String, Object> errorDetails) {
		exceptionDao.log(errorDetails);
	}
}
