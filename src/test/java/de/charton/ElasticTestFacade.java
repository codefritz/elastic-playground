package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.HealthReportRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ElasticTestFacade {
  private final ElasticsearchClient elasticsearchClient;

  void createIndex(String name) {
    try {
      CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
          .index(name)
          .settings(new IndexSettings.Builder()
              .numberOfShards("1")
              .numberOfReplicas("1")
              .build())
          .build();
      elasticsearchClient.indices().create(createIndexRequest);
    } catch (Exception e) {
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
