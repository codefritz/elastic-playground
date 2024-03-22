package de.charton;

import co.elastic.clients.elasticsearch.indices.CloneIndexRequest;
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
  public void createIndex(String indexName) {

    try {

      boolean value = true;
      enableWriteBlocks(indicesClient, true);

      CloneIndexRequest createIndexRequestBuilder =
          new CloneIndexRequest.Builder()
              .index(indexName)
              .target(indexName + "_clone")
              .build();

      indicesClient.clone(createIndexRequestBuilder);

      enableWriteBlocks(indicesClient, false);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("Creating index " + indexName);

  }

  private static void enableWriteBlocks(ElasticsearchIndicesClient indicesClient, boolean value) throws IOException {
    indicesClient
        .putSettings(put -> put
            .settings(stetting -> stetting
                .blocks(blocks -> {
                  return blocks
                      .write(value);
                })));
  }
}
