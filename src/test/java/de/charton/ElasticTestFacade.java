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
  private final ElasticsearchIndicesClient indicesClient;

  void createIndex(String name) {
    try {
      String testIndexName = name + "_org";
      indicesClient.create(create -> create.index(testIndexName)
          .settings(settings -> settings.numberOfShards("2").numberOfReplicas("2")));

      indicesClient.putAlias(alias -> alias.index(testIndexName).name(name));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      indicesClient.exists(cl -> cl.index(name));
      log.info("Settings: {}", indicesClient.getSettings(get -> get.index(name)).result());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
