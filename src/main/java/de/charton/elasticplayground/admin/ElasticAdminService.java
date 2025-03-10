package de.charton.elasticplayground.admin;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.Time.Builder;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ElasticAdminService {

  private static final Time REFRESH_DISABLED = new Builder().time("-1").build();
  private static final Time REFRESH_30_SEC = new Builder().time("30s").build();
  private final ElasticsearchIndicesClient indicesClient;
  private final ElasticsearchClient client;
  private final TimeBasedIndexNameGenerator indexNameGenerator;

  /**
   * Re-shard an index, including re-index from old index.
   */
  public void reShard(String indexName, int shards, int replicas) {
    try {
      String targetIndex = indexNameGenerator.generateIndexName(indexName);

      LOG.info("Creating index {} with {} shards and {} replicas", targetIndex, shards, replicas);
      indicesClient.create(create -> create.index(targetIndex)
          .settings(settings -> settings
              .numberOfShards(String.valueOf(shards))
              .numberOfReplicas(String.valueOf(replicas))));

      LOG.info("Reindexing {} to {}", indexName, targetIndex);

      indicesClient.putSettings(settings -> settings.index(indexName).settings(it -> it.refreshInterval(REFRESH_DISABLED)));
      client.reindex(reindex -> reindex.source(source -> source.index(indexName)).dest(dest -> dest.index(targetIndex)));

      LOG.info("Reindexing done, reset refresh interval to {}", REFRESH_30_SEC);
      indicesClient.putSettings(settings -> settings.index(indexName).settings(it -> it.refreshInterval(REFRESH_30_SEC)));

      switchAlias(indexName, targetIndex);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    LOG.info("Creating index {}", indexName);
  }

  private void switchAlias(String indexName, String targetIndex) throws IOException {
    String sourceIndex = indicesClient.getAlias(get -> get.index(indexName)).result().keySet().stream().findFirst().get();
    LOG.info("Deleting alias {} from {}", indexName, sourceIndex);
    LOG.info("Putting alias {} to {}", indexName, targetIndex);
    indicesClient.updateAliases(update -> update
        .actions(actions -> actions
            .remove(remove -> remove.index(sourceIndex).alias(indexName)))
        .actions(actions -> actions
            .add(add -> add.index(targetIndex).alias(indexName))
        ));
  }
}
