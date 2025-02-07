package in.hashconnect.http.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.http.client.exception.HttpException;
import in.hashconnect.http.client.exception.UnAuthorizedException;

public class DefaultHttpClient extends AbstractHttpClient implements HttpClient {
	private static final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);

	@Override
	public String doGet(String url) {
		return doGet(url, null);
	}

	@Override
	public String doGet(String url, Map<String, String> headers) {
		return doGet(url, headers, null);
	}

	@Override
	public String doGet(String url, Map<String, String> reqHeaders, Map<String, String> resHeaders) {
		InputStream in = null;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			if (reqHeaders != null) {
				for (String key : reqHeaders.keySet())
					con.setRequestProperty(key, reqHeaders.get(key));
			}

			if (con.getResponseCode() == UNAUTHORIZED) {
				throw new UnAuthorizedException();
			}

			in = con.getInputStream();
			if (resHeaders != null && con.getHeaderFields() != null) {
				for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
					List<String> values = entry.getValue();
					if (!values.isEmpty()) {
						resHeaders.put(entry.getKey(), values.get(0));
					}
				}
			}
			return IOUtils.toString(in, "utf-8");
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			InputStream errStream = con != null && con.getErrorStream() != null ? con.getErrorStream() : null;
			throw new HttpException(errStream, e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}
	}

	@Override
	public <T> String doPost(String url, T t) {
		return doPost(url, t, null);
	}

	@Override
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

			if (con.getResponseCode() == UNAUTHORIZED) {
				throw new UnAuthorizedException();
			}

			in = con.getInputStream();

			return IOUtils.toString(in, "utf-8");
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			InputStream errStream = con != null && con.getErrorStream() != null ? con.getErrorStream() : null;
			throw new HttpException(errStream, e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}
	}

	@Override
	public <T> String doPut(String url, T t, Map<String, String> headers) {
		HttpURLConnection con = null;
		InputStream in = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("PUT");
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

			if (con.getResponseCode() == UNAUTHORIZED) {
				throw new UnAuthorizedException();
			}

			in = con.getInputStream();

			return IOUtils.toString(in, "utf-8");
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			InputStream errStream = con != null && con.getErrorStream() != null ? con.getErrorStream() : null;
			throw new HttpException(errStream, e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}
	}

	@Override
	public <T> String doPatch(String url, T t, Map<String, String> headers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> String doDelete(String url, Map<String, String> headers) {
		throw new UnsupportedOperationException();
	}

	@Override
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
				throw new UnAuthorizedException("Unauthorized");
			}

			in = con.getInputStream();

			return IOUtils.toByteArray(in);
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("doPost failed", e);
		} finally {
			if (in != null)
				IOUtils.closeQuietly(in);
		}

		return null;
	}

}
