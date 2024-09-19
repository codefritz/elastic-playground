package de.charton;

import static org.assertj.core.api.Assertions.assertThat;

import de.charton.admin.ElasticAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(initializers = {ElasticInitializer.class})
@Import(ElasticServiceApp.class)
public class IndexReshardingIT {

  private static final String INDEX_NAME = "testikowski";

  @Autowired private ElasticTestFacade elasticTestFacade;
  @Autowired private ElasticAdminService elasticAdminService;

  @Test
  void shouldReshardIndex() {
    givenIndexWithDocuments();

    elasticAdminService.reShard(INDEX_NAME, 100, 1);

    elasticTestFacade.refresh(INDEX_NAME);
    elasticTestFacade.exists(INDEX_NAME);


  }

  private void givenIndexWithDocuments() {
    elasticTestFacade.createIndex(INDEX_NAME);

    for (int i = 0; i < 1000; i++) {
      elasticTestFacade.addDocument(INDEX_NAME, String.valueOf(i), "test");
    }
    elasticTestFacade.refresh(INDEX_NAME);
    elasticTestFacade.exists(INDEX_NAME);
  }
}
