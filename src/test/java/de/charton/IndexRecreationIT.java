package de.charton;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(initializers = {ElasticInitializer.class})
@Import(ElasticServiceApp.class)
public class IndexRecreationIT {

  @Autowired private ElasticTestFacade elasticTestFacade;
  @Autowired private ElasticAdminService elasticAdminService;
  @Test
  void should_load_context() {
    assertThat("this").isEqualTo("this");
  }

  @Test
  void should_create_index() {
    elasticTestFacade.createIndex("testikowski");
    elasticTestFacade.exists("testikowski");
    elasticTestFacade.addDocument("testikowski", "{\"name\":\"test\"}");


    elasticAdminService.reIndex("testikowski", 5, 3);

    elasticTestFacade.exists("testikowski");


  }
}
