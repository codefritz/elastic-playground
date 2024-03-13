package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CloneIndexRequest;
import co.elastic.clients.elasticsearch.indices.CloneIndexRequest.Builder;
import co.elastic.clients.util.ObjectBuilder;
import java.io.IOException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User: acharton
 * Date: 22.11.13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ElasticAdminService {

  private final ElasticsearchClient elasticsearchClient;
  public void createIndex(String indexName) {
    try {

      CloneIndexRequest createIndexRequestBuilder =
          new CloneIndexRequest.Builder()
              .index(indexName)
              .target(indexName + "_clone")
              .build();

        elasticsearchClient.indices().clone(createIndexRequestBuilder);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Creating index " + indexName);

  }
}
