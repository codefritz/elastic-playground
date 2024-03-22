package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import java.io.IOException;
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

  private final ElasticsearchIndicesClient indicesClient;
  private final ElasticsearchClient client;
  public void reIndex(String indexName, int shards, int replicas) {

    try {
      String targetIndex = indexName + "_clone";
      indicesClient.create(create -> create.index(targetIndex).settings(settings -> settings.numberOfShards(String.valueOf(shards)).numberOfReplicas(String.valueOf(replicas))));
      client.reindex(reindex -> reindex.source(source -> source.index(indexName)).dest(destination -> destination.index(targetIndex)));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Creating index " + indexName);

  }

}
