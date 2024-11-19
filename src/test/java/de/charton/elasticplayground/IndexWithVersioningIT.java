package de.charton.elasticplayground;

import static org.junit.Assert.assertThrows;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(initializers = {ElasticInitializer.class})
@Import(ElasticServiceApp.class)
@Slf4j
public class IndexWithVersioningIT {

  private static final String INDEX_NAME = "index_text";

  @Autowired private ElasticTestFacade elasticTestFacade;

  @Before
  public void setUp() {
    elasticTestFacade.createIndex(INDEX_NAME);
  }

  @After
  public void tearDown() {
    elasticTestFacade.deleteIndex(INDEX_NAME);
  }

  @Test
  void shouldThrowExceptionOnIndexSameDocument() {
    String id = String.valueOf(1);
    elasticTestFacade.addDocument(INDEX_NAME, id, "bmw");

    assertThrows(Exception.class, () -> {
      elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 1);
    });
  }

  @Test
  void shouldThrowExceptionOnIndexSameDocumentVersion() {
    String id = String.valueOf(2);
    elasticTestFacade.addDocument(INDEX_NAME, id, "bmw");
    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 1);
    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 2);

    assertThrows(Exception.class, () -> {
      elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 2);
    });

    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 3);
  }

  @Test
  void shouldThrowExceptionOnIndexSameDocumentVersionWhileSkipVersions() {
    String id = String.valueOf(3);
    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 1);
    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 100);

    assertThrows(Exception.class, () -> {
      elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 50);
    });

    elasticTestFacade.addDocumentWithExternalVersion(INDEX_NAME, id, "bmw", 300);
  }

}
