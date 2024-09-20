package de.charton.elasticplayground;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import java.time.Duration;
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
    container.withExposedPorts(9200)
        .withEnv("xpack.security.enabled", "false")
        .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
        .withStartupTimeout(Duration.ofSeconds(30))
        .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
            new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(9200), new ExposedPort(9200)))
        ));

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
