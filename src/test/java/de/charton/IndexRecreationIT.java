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

  @Autowired private ElasticAdminService elasticAdminService;
  @Test
  void should_load_context() {
    assertThat("this").isEqualTo("this");
  }

  @Test
  void should_create_index() {
    elasticAdminService.createIndex("testikowski");
  }
}
