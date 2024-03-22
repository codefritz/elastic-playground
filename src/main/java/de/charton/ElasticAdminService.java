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
  private final TimeBasedIndexNameGenerator indexNameGenerator;

  public void reIndex(String indexName, int shards, int replicas) {

    try {
      String targetIndex = indexNameGenerator.generateIndexName(indexName);

      log.info("Creating index " + targetIndex + " with " + shards + " shards and " + replicas + " replicas");
      indicesClient.create(create -> create.index(targetIndex)
          .settings(settings -> settings.numberOfShards(String.valueOf(shards)).numberOfReplicas(String.valueOf(replicas))));

      log.info("Reindexing " + indexName + " to " + targetIndex);
      client.reindex(reindex -> reindex.source(source -> source.index(indexName)).dest(dest -> dest.index(targetIndex)));

      switchAlias(indexName, targetIndex);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Creating index " + indexName);

  }

  private void switchAlias(String indexName, String targetIndex) throws IOException {
    String sourceIndex = indicesClient.getAlias(get -> get.index(indexName)).result().keySet().stream().findFirst().get();
    log.info("Deleting alias " + indexName + " from " + sourceIndex);
    log.info("Putting alias " + indexName + " to " + targetIndex);
    indicesClient.updateAliases(update -> update
        .actions(actions -> actions
            .remove(remove -> remove.index(sourceIndex).alias(indexName)))
        .actions(actions -> actions
            .add(add -> add.index(targetIndex).alias(indexName))
        ));
  }
}
