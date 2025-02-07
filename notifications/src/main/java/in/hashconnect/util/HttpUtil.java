package in.hashconnect.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.hashconnect.common.exception.UnauthorizedException;
import in.hashconnect.common.util.JsonUtil;
import in.hashconnect.common.util.SettingsUtil;
import in.hashconnect.common.util.StringUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	@Resource
	private ObjectMapper mapper = null;

	@Resource
	private SettingsUtil settingsUtil;

	public String doGet(String url) {
		return doGet(url, null);
	}

	public boolean isUrlValid(String url) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

			if (con.getResponseCode() != HttpServletResponse.SC_OK) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String doGet(String url, Map<String, String> headers) {
		InputStream in = null;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			if (headers != null) {
				for (String key : headers.keySet())
					con.setRequestProperty(key, headers.get(key));
			}

			if (con.getResponseCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				throw new UnauthorizedException("Unauthorized");
			}

			in = con.getInputStream();

			return IOUtils.toString(in, "utf-8");
		} catch (UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("doGet failed", e);
			if (con != null && con.getErrorStream() != null) {
				in = con.getErrorStream();
				try {
					return IOUtils.toString(in);
				} catch (IOException e1) {
					logger.error("doGet failed to read errorStream as well" + e.getMessage());
				}
			}
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

	public <T> String doPost(String url, T t) {
		return doPost(url, t, null);
	}

	public <T> String doPost(String url, T t, Map<String, String> headers) {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(Boolean.TRUE);
			con.setDoOutput(Boolean.TRUE);
			if (headers != null)
				for (String key : headers.keySet())
					con.addRequestProperty(key, headers.get(key));

			if (t != null) {
				String request = convert(t);
				OutputStream out = con.getOutputStream();
				out.write(request.getBytes());
				out.flush();
			}

			if (con.getResponseCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				in = con.getErrorStream();
				throw new UnauthorizedException(getErrorContent(in));
			}

			in = con.getInputStream();

			return IOUtils.toString(in, "utf-8");
		} catch (UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("doPost failed", e);

			if (con != null && con.getErrorStream() != null) {
				in = con.getErrorStream();
				return getErrorContent(in);
			}
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

	private String getErrorContent(InputStream in) {
		if (in == null)
			return null;
		try {
			return IOUtils.toString(in);
		} catch (IOException e1) {
		}
		return null;
	}

	public <T> byte[] doGetForBytes(String url, Map<String, String> headers) {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			if (headers != null) {
				for (String key : headers.keySet())
					con.setRequestProperty(key, headers.get(key));
			}

			if (con.getResponseCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				throw new UnauthorizedException("Unauthorized");
			}

			in = con.getInputStream();

			return IOUtils.toByteArray(in);
		} catch (UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("doPost failed", e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public <T> String doPostUsingHttpClient(String url, T t, Map<String, String> headers) throws Exception {
		if (t == null)
			return null;

		HttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		for (String key : headers.keySet())
			post.addHeader(key, headers.get(key));

		if (t instanceof Map) {
			Map<String, String> params = (Map<String, String>) t;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			for (String key : params.keySet())
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} else if (t instanceof String) {
			String body = (String) t;
			post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
		} else {
			String body = JsonUtil.toString(t);
			post.setEntity(new StringEntity(body));
		}

		HttpResponse response = client.execute(post);

		String strResponse = IOUtils.toString(response.getEntity().getContent());

		return strResponse;
	}

	@SuppressWarnings({ "unchecked" })
	public <T> String doPutUsingHttpClient(String url, T t, Map<String, String> headers) throws Exception {
		if (t == null)
			return null;

		HttpClient client = HttpClients.createDefault();
		HttpPut put = new HttpPut(url);

		for (String key : headers.keySet())
			put.addHeader(key, headers.get(key));

		if (t instanceof Map) {
			Map<String, String> params = (Map<String, String>) t;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			for (String key : params.keySet())
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));

			put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} else if (t instanceof String) {
			String body = (String) t;
			put.setEntity(new StringEntity(body));
		}

		HttpResponse response = client.execute(put);
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new UnauthorizedException();
		}

		String strResponse = IOUtils.toString(response.getEntity().getContent());

		return strResponse;
	}

	@SuppressWarnings({ "unchecked" })
	public <T> String doPatchUsingHttpClient(String url, T t, Map<String, String> headers) throws Exception {
		if (t == null)
			return null;

		HttpClient client = HttpClients.createDefault();
		HttpPatch put = new HttpPatch(url);

		for (String key : headers.keySet())
			put.addHeader(key, headers.get(key));

		if (t instanceof Map) {
			Map<String, String> params = (Map<String, String>) t;

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			for (String key : params.keySet())
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));

			put.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} else if (t instanceof String) {
			String body = (String) t;
			put.setEntity(new StringEntity(body));
		}

		HttpResponse response = client.execute(put);

		String strResponse = IOUtils.toString(response.getEntity().getContent());

		return strResponse;
	}

	public <T> String doDelete(String url, Map<String, String> headers) throws Exception {
		HttpClient client = HttpClients.createDefault();
		HttpDelete put = new HttpDelete(url);

		for (String key : headers.keySet())
			put.addHeader(key, headers.get(key));

		HttpResponse response = client.execute(put);
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new UnauthorizedException();
		}

		return IOUtils.toString(response.getEntity().getContent());
	}

	public String doSSLGet(String url) {
		InputStream in = null;
		try {
			HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
			setAcceptAllVerifier(con);

			in = con.getInputStream();

			return IOUtils.toString(in);
		} catch (Exception e) {
			logger.error("doGet failed", e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

	protected static void setAcceptAllVerifier(HttpsURLConnection connection)
			throws NoSuchAlgorithmException, KeyManagementException {

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, ALL_TRUSTING_TRUST_MANAGER, new java.security.SecureRandom());
		SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

		connection.setSSLSocketFactory(sslSocketFactory);

		// Since we may be using a cert with a different name, we need to ignore
		// the hostname as well.
		connection.setHostnameVerifier(ALL_TRUSTING_HOSTNAME_VERIFIER);
	}

	private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	} };

	private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	private <T> String convert(T t) {
		if (t instanceof String)
			return (String) t;
		try {
			return mapper.writeValueAsString(t);
		} catch (Exception e) {
			logger.error("failed to convert object to string due to " + e.getMessage());
			return null;
		}
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static Map<String, String> paramsToMap(HttpServletRequest request) {
		Map<String, String> params = new HashMap<>();
		try {

			String reqString = IOUtils.toString(request.getInputStream());
			final List<NameValuePair> values = URLEncodedUtils.parse(reqString, StandardCharsets.UTF_8);

			for (NameValuePair n : values)
				params.put(n.getName(), n.getValue());
		} catch (IOException e1) {
		}
		return params;
	}

	public static Map<String, String> paramsToMap(HttpServletRequest request, String method) {
		Map<String, String> params = new HashMap<>();
		try {
			String reqString = null;
			if ("POST".equalsIgnoreCase(method)) {
				reqString = IOUtils.toString(request.getInputStream());
			} else if ("GET".equalsIgnoreCase(method)) {
				reqString = request.getQueryString();
			}

			final List<NameValuePair> values = URLEncodedUtils.parse(reqString, StandardCharsets.UTF_8);

			for (NameValuePair n : values)
				params.put(n.getName(), n.getValue());
		} catch (IOException e1) {
		}
		return params;
	}

	public Cookie createCookie(String name, String value, int maxAge) {
		Cookie c = new Cookie(name, value);
		if (maxAge > 0)
			c.setMaxAge(maxAge);
		String cookiedata = settingsUtil.getValue("cookie_params");
		@SuppressWarnings("unchecked")
		Map<String, String> params = JsonUtil.readValue(cookiedata, HashMap.class);

		if (params.containsKey("domain"))
			c.setDomain(params.get("domain"));
		if (params.containsKey("path"))
			c.setPath(params.get("path"));
		if (params.containsKey("httpOnly"))
			c.setHttpOnly(Boolean.valueOf(params.get("httpOnly")));
		if (params.containsKey("secure"))
			c.setSecure(Boolean.valueOf(params.get("secure")));

		return c;
	}

	public Cookie expireCookie(String name) {
		Cookie outCookie = new Cookie(name, "");
		outCookie.setMaxAge(0);

		String cookiedata = settingsUtil.getValue("cookie_params");
		@SuppressWarnings("unchecked")
		Map<String, String> params = JsonUtil.readValue(cookiedata, HashMap.class);

		if (params.containsKey("domain"))
			outCookie.setDomain(params.get("domain"));
		if (params.containsKey("path"))
			outCookie.setPath(params.get("path"));
		if (params.containsKey("httpOnly"))
			outCookie.setHttpOnly(Boolean.valueOf(params.get("httpOnly")));
		if (params.containsKey("secure"))
			outCookie.setSecure(Boolean.valueOf(params.get("secure")));

		return outCookie;
	}

	public Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie c : cookies) {
			if (name.equals(c.getName()))
				return c;
		}
		return null;
	}

	public static String getUserIP(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
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

	public <T> byte[] doPostReturnBytes(String url, T t, Map<String, String> headers) {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(Boolean.TRUE);
			con.setDoOutput(Boolean.TRUE);
			if (headers != null)
				for (String key : headers.keySet())
					con.addRequestProperty(key, headers.get(key));

			String request = convert(t);

			OutputStream out = con.getOutputStream();
			out.write(request.getBytes());
			out.flush();

			if (con.getResponseCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				throw new UnauthorizedException();
			}

			in = con.getInputStream();

			return IOUtils.toByteArray(in);
		} catch (UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("doPost failed", e);

			if (con != null && con.getErrorStream() != null) {
				in = con.getErrorStream();
				try {
					return IOUtils.toByteArray(in);
				} catch (IOException e1) {
					logger.error("doPost failed to read errorStream as well" + e.getMessage());
				}
			}
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

	public byte[] doGetInStreamWithRedirectResolver(String url, Map<String, String> headers) {
		InputStream in = null;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet())
					con.setRequestProperty(entry.getKey(), entry.getValue());
			}
			if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
					|| con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				String redirectUrl = con.getHeaderField("Location");
				return doGetInStreamWithRedirectResolver(redirectUrl, null);
			}
			in = con.getInputStream();
			return IOUtils.toByteArray(in);
		} catch (Exception e) {
			logger.error("S3 Upload Failed", e);
			if (con != null && con.getErrorStream() != null) {
				in = con.getErrorStream();
				try {
					return IOUtils.toByteArray(in);
				} catch (Exception e1) {
					logger.error("doGetInStream failed to read errorStream as well" + e.getMessage());
					throw new RuntimeException("S3 Upload Failed");
				}
			}
			throw new RuntimeException("S3 Upload Failed");
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}
	}

	public static void disableSSLVerification() throws NoSuchAlgorithmException, KeyManagementException {
		// Set up a TrustManager that accepts all certificates
		TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				// No check, accept all client certificates
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				// No check, accept all server certificates
			}
		} };

		// Set up SSLContext with the trust manager that accepts all certificates
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

	}
}
