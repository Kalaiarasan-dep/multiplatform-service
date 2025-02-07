package in.hashconnect.http.client;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.http.client.exception.HttpException;
import in.hashconnect.http.client.exception.UnAuthorizedException;
import in.hashconnect.util.JsonUtil;

public class ApacheHttpClient extends AbstractHttpClient implements HttpClient {
	private static final Logger logger = LoggerFactory.getLogger(ApacheHttpClient.class);

	@Override
	public String doGet(String url) {
		return doGet(url, null);
	}

	@Override
	public String doGet(String url, Map<String, String> headers) {
		try {
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);

			if (headers != null)
				for (String key : headers.keySet())
					get.addHeader(key, headers.get(key));

			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new UnAuthorizedException();
			}

			return IOUtils.toString(response.getEntity().getContent());
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}
	@Override
	public String doGet(String url, Map<String, String> reqHeaders,Map<String, String> resHeaders) {
		return doGet(url, reqHeaders);
	}
	@Override
	public <T> String doPost(String url, T t) {
		return doPost(url, t, null);
	}

	@Override
	public <T> String doPost(String url, T t, Map<String, String> headers) {
//		if (t == null)
//			return null;

		try {
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);

			if(MapUtils.isNotEmpty(headers)) {
				for (String key : headers.keySet())
					post.addHeader(key, headers.get(key));
			}

			if (t instanceof Map) {
				Map<String, String> params = (Map<String, String>) t;

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				for (String key : params.keySet())
					nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} else if (t instanceof String) {
				String body = (String) t;
				post.setEntity(new StringEntity(body,StandardCharsets.UTF_8));
			} else {
				String body = JsonUtil.toString(t);
				post.setEntity(new StringEntity(body,StandardCharsets.UTF_8));
			}

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new UnAuthorizedException();
			}
			return IOUtils.toString(response.getEntity().getContent(),"utf-8");
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	@Override
	public <T> String doPut(String url, T t, Map<String, String> headers) {
		if (t == null)
			return null;

		try {
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
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
				throw new UnAuthorizedException();
			}

			return IOUtils.toString(response.getEntity().getContent());
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	@Override
	public <T> String doPatch(String url, T t, Map<String, String> headers) {
		if (t == null)
			return null;

		try {
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
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
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new UnAuthorizedException();
			}

			return IOUtils.toString(response.getEntity().getContent());
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(e);
		}

	}

	@Override
	public <T> String doDelete(String url, Map<String, String> headers) {
		try {
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
			HttpDelete put = new HttpDelete(url);

			for (String key : headers.keySet())
				put.addHeader(key, headers.get(key));

			HttpResponse response = client.execute(put);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new UnAuthorizedException();
			}

			return IOUtils.toString(response.getEntity().getContent());
		} catch (UnAuthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	@Override
	public <T> byte[] doGetForBytes(String url, Map<String, String> headers) {
		// TODO Auto-generated method stub
		return null;
	}

}
