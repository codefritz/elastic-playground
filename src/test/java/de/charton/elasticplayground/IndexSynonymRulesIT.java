package de.charton.elasticplayground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(initializers = {ElasticInitializer.class})
@Import(ElasticServiceApp.class)
public class IndexSynonymRulesIT {

  private static final String INDEX_NAME = "testikowski";

  @Autowired private ElasticTestFacade elasticTestFacade;
  @Test
  void shouldUseSynonym() {
    elasticTestFacade.createIndex(INDEX_NAME);
    elasticTestFacade.addSynonym("TV -> patchenkino");

    elasticTestFacade.addDocument(INDEX_NAME, "1", "TV");
    elasticTestFacade.addDocument(INDEX_NAME, "2", "Samsung");


    elasticTestFacade.refresh(INDEX_NAME);

  }

}
