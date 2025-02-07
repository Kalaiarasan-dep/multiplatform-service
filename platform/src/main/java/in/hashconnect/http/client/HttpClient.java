package in.hashconnect.http.client;

import java.util.Map;

public interface HttpClient {
	public String doGet(String url);

	public String doGet(String url, Map<String, String> headers);

	String doGet(String url, Map<String, String> reqHeaders, Map<String, String> resHeaders);

	public <T> String doPost(String url, T t);

	public <T> String doPost(String url, T t, Map<String, String> headers);

	public <T> String doPut(String url, T t, Map<String, String> headers);

	public <T> String doPatch(String url, T t, Map<String, String> headers);

	public <T> String doDelete(String url, Map<String, String> headers);
	
	public <T> byte[] doGetForBytes(String url, Map<String, String> headers);
}
