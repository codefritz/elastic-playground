package de.charton.elasticplayground.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elasticsearch")
@RequiredArgsConstructor
class ElasticProperties {
  private final String host;
  private final String port;

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }
}
