package de.charton;

import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
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
      elasticsearchClient.create(create -> create.index(name)
          .settings(settings -> settings.numberOfShards("2").numberOfReplicas("2")));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      elasticsearchClient.exists(cl -> cl.index(name));
      log.info("Settings: {}", elasticsearchClient.getSettings(get -> get.index(name)).result());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
