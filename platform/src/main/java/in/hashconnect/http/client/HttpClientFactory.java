package in.hashconnect.http.client;

public class HttpClientFactory {
	public static enum TYPE {
		sun, apache
	};

	private static HttpClient defaultClient = new DefaultHttpClient();
	private static HttpClient apacheClient = new ApacheHttpClient();

	public static HttpClient get(TYPE type) {
		if (type == TYPE.apache) {
			return apacheClient;
		}
		return defaultClient;
	}
}
