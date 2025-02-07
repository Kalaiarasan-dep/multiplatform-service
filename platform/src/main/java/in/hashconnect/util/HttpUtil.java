package in.hashconnect.util;

import static in.hashconnect.util.StringUtil.isValid;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (!isValid(ip)) {
			return request.getRemoteAddr();
		}
		return ip;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	public static String mapToQueryParams(Map<String, Object> params) {
		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			String value = String.valueOf(params.get(key));
			if (StringUtil.isValid(value))
				try {
					sb.append(key).append("=").append(URLEncoder.encode(value.trim(), "utf8")).append("&");
				} catch (Exception e) {
				}
		}
		return sb.toString();
	}

	public static Cookie createCookie(String domain, String path, boolean httpOnly, boolean secure, String name,
			String value, int maxAge) {
		Cookie c = new Cookie(name, value);

		if (maxAge > 0)
			c.setMaxAge(maxAge);

		c.setDomain(domain);
		c.setPath(path);
		c.setHttpOnly(httpOnly);
		c.setSecure(secure);

		return c;
	}
}
