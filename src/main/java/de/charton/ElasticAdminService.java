package de.charton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * User: acharton
 * Date: 22.11.13
 */
@Component
@Slf4j
public class ElasticAdminService {
  public void createIndex(String indexName) {
    log.info("Creating index " + indexName);
  }
}
