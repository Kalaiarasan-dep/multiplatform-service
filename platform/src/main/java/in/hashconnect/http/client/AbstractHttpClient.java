package in.hashconnect.http.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.util.JsonUtil;

public abstract class AbstractHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpClient.class);
	protected final int UNAUTHORIZED = 401;

	protected <T> String convert(T t) {
		if (t instanceof String)
			return (String) t;
		try {
			return JsonUtil.toString(t);
		} catch (Exception e) {
			logger.error("failed to convert object to string due to " + e.getMessage());
			return null;
		}
	}

	protected String getErrorContent(InputStream in) {
		if (in == null)
			return null;
		try {
			return IOUtils.toString(in);
		} catch (IOException e1) {
		}
		return null;
	}

}
