package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ElasticTestFacade {
  private final ElasticsearchClient elasticsearchClient;

  void createIndex(String name) {
    try {
      CreateIndexRequest cir = new CreateIndexRequest.Builder().index(name).build();
      elasticsearchClient.indices().create(cir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      elasticsearchClient.exists(new ExistsRequest.Builder().index(name).build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
