package de.charton.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elasticsearch")
@Getter
@RequiredArgsConstructor
class ElasticProperties {
  private final String host;
  private final String port;
}
