package de.charton.elasticplayground;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.OpType;
import co.elastic.clients.elasticsearch._types.VersionType;
import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.synonyms.ElasticsearchSynonymsClient;
import co.elastic.clients.elasticsearch.synonyms.PutSynonymRequest;
import co.elastic.clients.elasticsearch.synonyms.SynonymRule;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
          .id(id)
          .document(Document.builder().id(id).title(null).description(text).build()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void addDocumentWithExternalVersion(String indexName, String id, String text, long version) {
    try {
      esClient.index(index -> index
          .index(indexName)
          .document(Document.builder().id(id).title(null).description(text).build())
          .id(id).version(version).versionType(VersionType.External).opType(OpType.Index));
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
      InputStream mappingJson = new ByteArrayInputStream("""
          {
            "properties": {
              "id": {
                "type": "long"
              },
              "title": {
                "type": "text"
              },
              "description": {
                "type": "text"
              }
            }
          }
          """.getBytes(StandardCharsets.UTF_8));
      indicesClient.create(create -> create.index(testIndexName).mappings(mapping -> mapping.withJson(mappingJson).dynamic(DynamicMapping.Strict))
          .settings(settings -> settings.numberOfShards("50").numberOfReplicas("2"))
      );
      indicesClient.putAlias(alias -> alias.index(testIndexName).name(name));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void addSynonym(String synonym) {
    PutSynonymRequest putSynonymRequest =
        PutSynonymRequest.of(it -> it.id("synonyms_set_test1")
            .synonymsSet(new SynonymRule.Builder().id("r1").synonyms(synonym).build()));
    try {
      // synonymsClient.putSynonymRule(PutSynonymRuleRequest.of(it -> it.setId("synonyms_set_test1").ruleId("r1").synonyms(synonym)));
      synonymsClient.putSynonym(putSynonymRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void exists(String name) {
    try {
      indicesClient.exists(cl -> cl.index(name));
      LOG.info("Settings: {}", indicesClient.getSettings(get -> get.index(name)).result());
      long indexCount = esClient.count(count -> count.index(name)).count();
      LOG.info("Index count: {}", indexCount);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteIndex(String indexName) {
    try {
      indicesClient.delete(delete -> delete.index(indexName)).wait();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
