package de.charton;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.elasticsearch.ElasticsearchContainer;


public class ElasticInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final String IMAGE_NAME =
      "docker.elastic.co/elasticsearch/elasticsearch:8.0.0";

  private static final ElasticsearchContainer container =
      new ElasticsearchContainer(IMAGE_NAME)
          .withExposedPorts(9200);
  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    // remove from environment to have TLS by default enabled
    container.getEnvMap().remove("xpack.security.enabled");

    // custom wait strategy not requiring any TLS tuning...
    container.setWaitStrategy(
        new LogMessageWaitStrategy().withRegEx(".*\"message\":\"started\".*"));

    container.start();

    TestPropertyValues.of(
        String.format("elasticsearch.host=%s", container.getHost()),
        String.format("elasticsearch.port=%s", container.getMappedPort(9200))
    ).applyTo(applicationContext);

  }
}
