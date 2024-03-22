package de.charton;

import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest.Builder;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ElasticTestFacade {
  private final ElasticsearchIndicesClient elasticsearchClient;

  void createIndex(String name) {
    try {
      CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
          .index(name)
          .settings(new IndexSettings.Builder()
              .numberOfShards("2")
              .numberOfReplicas("2")
              .build())
          .build();
      elasticsearchClient.create(createIndexRequest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      ExistsRequest build = new Builder().index(name).build();
      elasticsearchClient.exists(build).value();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
