package in.hashconnect.elastic.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class ESClient {
	private static final Logger logger = LoggerFactory.getLogger(ESClient.class);

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	public void create(String index) {
		try {
			elasticsearchClient.create(c -> c.index(index));
		} catch (ElasticsearchException | IOException e) {
			logger.error("failed to create", e);
		}
	}

	public void insert(String index, String type, Document document) {
		try {
			elasticsearchClient.index(i -> i.index(index).id(document.getId()).document(document));
		} catch (ElasticsearchException | IOException e) {
			logger.error("failed to insert", e);
		}
	}

	public void update(String index, String type, Document document) {
		try {
			elasticsearchClient.index(i -> i.index(index).id(document.getId()).document(document));
		} catch (ElasticsearchException | IOException e) {
			logger.error("failed to update", e);
		}
	}

	public void delete(String index, String type, String id) {
		try {
			elasticsearchClient.delete(i -> i.index(index).id(id));
		} catch (ElasticsearchException | IOException e) {
			logger.error("failed to delete", e);
		}
	}

	public <T> List<T> search(String index, String type, String searchField, String keyword, Class<T> pClas) {
		try {
			SearchResponse<T> response = elasticsearchClient
					.search(r -> r.index(index).query(q -> q.match(t -> t.field(searchField).query(keyword))), pClas);

			if (response == null || CollectionUtils.isEmpty(response.hits().hits())) {
				logger.info("no items found for search params index: {}, type: {}, keyword: {}", index, type, keyword);
			}

			return response.hits().hits().stream().map(h -> h.source()).collect(Collectors.toList());
		} catch (ElasticsearchException | IOException e) {
			logger.error("search failed", e);
		}
		return Collections.emptyList();
	}

	public <T> List<T> searchWithFuzzy(String index, String type, String keyword,String field, Class<T> pClas) throws IOException {

			SearchResponse<T> response = elasticsearchClient.search(
					s -> s.index(index).query(q -> q.fuzzy(t -> t.field(field).value(keyword).fuzziness("auto"))),
					pClas);

			if (response == null || response.hits().hits().isEmpty()) {
				logger.info("no items found for search params. keyword: " + keyword);
			}

			return response.hits().hits().stream().map(h -> h.source()).collect(Collectors.toList());

	}


}
