package de.charton.elasticplayground.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.synonyms.ElasticsearchSynonymsClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ElasticClientConfiguration {

  private static final Logger log = LoggerFactory.getLogger(ElasticClientConfiguration.class);

  @Bean
  public ElasticsearchClient elasticSearchClient(ElasticProperties elasticsearchProperties) {
    // URL and API key
    String serverUrl = getServerUrl(elasticsearchProperties);
    log.info("Creating elastic rest client connecting to host {}", serverUrl);

    // Create the low-level client
    RestClient restClient = RestClient
        .builder(HttpHost.create(serverUrl))
        .build();

    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
        restClient, new JacksonJsonpMapper());

    // And create the API client
    return new ElasticsearchClient(transport);
  }

  private static String getServerUrl(ElasticProperties elasticsearchProperties) {
    return elasticsearchProperties.getHost() + ":" + elasticsearchProperties.getPort();
  }

  @Bean
  public ElasticsearchIndicesClient elasticSearchIndicesClient(ElasticsearchClient elasticsearchClient) {
    return elasticsearchClient.indices();
  }

  @Bean
  public ElasticsearchSynonymsClient elasticSearchSynonymsClient(ElasticsearchClient elasticsearchClient) {
    return elasticsearchClient.synonyms();
  }

}
