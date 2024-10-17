package de.charton.elasticplayground;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticSearchService {

  private final ElasticsearchClient client;

  public List<Document> search(String keyword, String index, String... indices) {
    try {
      SearchResponse<Document> response = client.search(search -> search.index(index, indices).query(query -> query.term(term -> term.field("description").value(keyword))), Document.class);
      return response.hits().hits().stream().map(it -> it.source()).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
