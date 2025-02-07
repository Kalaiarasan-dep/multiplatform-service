package in.hashconnect.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {
	private static ObjectMapper mapper = new ObjectMapper();
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		mapper.registerModule(new JavaTimeModule());
	}

	public static <T> T convert(Object data, Class<T> pclas) {
		return (T) mapper.convertValue(data, pclas);
	}

	public static <T> T convert(Object data, TypeReference<T> pclas) {
		return (T) mapper.convertValue(data, pclas);
	}

	public static String toString(Object data) {
		if (data == null)
			return null;

		if (data instanceof String && !StringUtil.isValid((String) data))
			return null;

		try {
			String out = mapper.writeValueAsString(data);

			if (StringUtil.isValid(out))
				return out;
		} catch (IOException e) {
			logger.error("toString failed", e);
		}

		return null;
	}

	public static <T> T readValue(InputStream in, Class<T> pClass) {
		if (in == null)
			return null;
		try {
			return mapper.readValue(in, pClass);
		} catch (IOException e) {
			logger.error("readValue(InputStream) failed", e);
			return null;
		}
	}

	public static <T> T readValue(URL url, Class<T> pClass) {
		if (url == null)
			return null;
		try {
			return mapper.readValue(url.openStream(), pClass);
		} catch (IOException e) {
			logger.error("readValue(URL) failed", e);
			return null;
		}
	}

	public static <T> T readValue(String in, Class<T> pClass) {
		if (!StringUtil.isValid(in))
			return null;
		try {
			return mapper.readValue(in, pClass);
		} catch (Exception e) {
			logger.error("readValue(String) failed", e);
			throw new RuntimeException(e);
		}
	}

	public static <T> T readValue(String in, TypeReference<T> type) {
		if (!StringUtil.isValid(in))
			return null;
		try {
			return mapper.readValue(in, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
