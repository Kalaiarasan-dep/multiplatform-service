package in.hashconnect;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import in.hashconnect.elastic.search.ESClient;

@Configuration
@ConditionalOnProperty(value = "elastic.search.enabled", havingValue = "true", matchIfMissing = false)
public class ElasticSearchConfig {

	@Value("${elastci.search.api.url}")
	private String url;
	@Value("${elastic.search.api.key}")
	private String apiKey;

	@Bean
	public ElasticsearchClient elasticSearchClient() {
		RestClient restClient = RestClient.builder(HttpHost.create(url))
				.setDefaultHeaders(new Header[] { new BasicHeader("Authorization", "ApiKey " + apiKey) }).build();

		RestClientOptions options = new RestClientOptions(RequestOptions.DEFAULT);
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(), options);

		return new ElasticsearchClient(transport);
	}

	@Bean
	public ESClient esClient() {
		return new ESClient();
	}
}
