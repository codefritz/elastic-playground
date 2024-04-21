package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.synonyms.ElasticsearchSynonymsClient;
import co.elastic.clients.elasticsearch.synonyms.PutSynonymRequest;
import co.elastic.clients.elasticsearch.synonyms.SynonymRule;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ElasticTestFacade {
  private final ElasticsearchIndicesClient indicesClient;
  private final ElasticsearchClient esClient;
  private final ElasticsearchSynonymsClient synonymsClient;

  void addDocument(String indexName, String id, String text) {
    try {
      esClient.index(index -> index
          .index(indexName)
          .document(new Document(id, text)).id(id));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void refresh(String indexName) {
    try {
      indicesClient.refresh(refresh -> refresh.index(indexName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void createIndex(String name) {
    try {
      String testIndexName = name + "_org";
      indicesClient.create(create -> create.index(testIndexName)
          .settings(settings -> settings.numberOfShards("2").numberOfReplicas("2"))
      );
      indicesClient.putAlias(alias -> alias.index(testIndexName).name(name));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void addSynonym(String synonym) {
    PutSynonymRequest putSynonymRequest =
        PutSynonymRequest.of(it -> it.id("syn_rule_42").synonymsSet(SynonymRule.of(rule -> rule.synonyms(synonym))));
    try {
      synonymsClient.putSynonym(putSynonymRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      indicesClient.exists(cl -> cl.index(name));
      log.info("Settings: {}", indicesClient.getSettings(get -> get.index(name)).result());
      long indexCount = esClient.count(count -> count.index(name)).count();
      log.info("Index count: {}", indexCount);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
