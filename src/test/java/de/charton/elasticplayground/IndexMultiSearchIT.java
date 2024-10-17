package de.charton.elasticplayground;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(initializers = {ElasticInitializer.class})
@Import(ElasticServiceApp.class)
@Slf4j
public class IndexMultiSearchIT {

  private static final String INDEX_NAME_GOODS = "index_goods";
  private static final String INDEX_NAME_CARS = "index_vehicles";

  @Autowired private ElasticTestFacade elasticTestFacade;
  @Autowired private ElasticSearchService elasticSearchService;

  @Test
  void shouldMatchDocumentsFromMultiIndizies() {
    elasticTestFacade.createIndex(INDEX_NAME_CARS);
    elasticTestFacade.createIndex(INDEX_NAME_GOODS);

    elasticTestFacade.addDocument(INDEX_NAME_CARS, String.valueOf(1), "bmw");
    elasticTestFacade.addDocument(INDEX_NAME_CARS, String.valueOf(2), "mercedes kombi");
    elasticTestFacade.addDocument(INDEX_NAME_CARS, String.valueOf(3), "wartburg kombi");
    elasticTestFacade.addDocument(INDEX_NAME_GOODS, String.valueOf(4), "wartburg kombi spielzeug");

    elasticTestFacade.refresh(INDEX_NAME_CARS);
    elasticTestFacade.refresh(INDEX_NAME_GOODS);

    assertThat(search("bmw")).map(doc -> doc.id()).containsExactly("1");
    assertThat(search("mercedes")).map(doc -> doc.id()).containsExactly("2");
    assertThat(search("wartburg")).map(doc -> doc.id()).containsExactlyInAnyOrder("3", "4");
    assertThat(search("kombi")).map(doc -> doc.id()).containsExactlyInAnyOrder("2", "3", "4");
  }

  private List<Document> search(String keyword) {
    return elasticSearchService.search(keyword, INDEX_NAME_CARS, INDEX_NAME_GOODS);
  }
}
