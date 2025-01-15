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

  /**
   * Search for a keyword in the given index and indices.
   * @param keyword Search keyword.
   * @param index Index (main) to search in.
   * @param indices Optional, in case you want to search in multiple indices.
   */
  public List<Document> search(String keyword, String index, String... indices) {
    try {
      SearchResponse<Document> response = client.search(search -> search.index(index, indices).query(query -> query.term(term -> term.field("description").value(keyword))), Document.class);
      return response.hits().hits().stream().map(it -> it.source()).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
