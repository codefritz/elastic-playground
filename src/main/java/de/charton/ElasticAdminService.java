package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CloneIndexRequest;
import co.elastic.clients.elasticsearch.indices.CloneIndexRequest.Builder;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.IndexSettingBlocks;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
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

    ElasticsearchIndicesClient indicesClient = elasticsearchClient.indices();

    try {

      indicesClient
          .putSettings(put -> put
              .settings(stetting -> stetting
                  .blocks(blocks -> blocks
                      .write(true))));

      CloneIndexRequest createIndexRequestBuilder =
          new CloneIndexRequest.Builder()
              .index(indexName)
              .target(indexName + "_clone")
              .build();

        indicesClient.clone(createIndexRequestBuilder);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Creating index " + indexName);

  }
}
