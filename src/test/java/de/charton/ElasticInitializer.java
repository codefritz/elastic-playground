package de.charton;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.elasticsearch.ElasticsearchContainer;


public class ElasticInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private static final String IMAGE_NAME =
      "docker.elastic.co/elasticsearch/elasticsearch:8.0.0";

  private static final ElasticsearchContainer container =
      new ElasticsearchContainer(IMAGE_NAME)
          .withExposedPorts(9200)
          .withPassword("s3cret");

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    container.start();

    HttpHost host = new HttpHost("localhost", container.getMappedPort(9200), "https");
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "s3cret"));
    final RestClientBuilder builder = RestClient.builder(host);

    builder.setHttpClientConfigCallback(clientBuilder -> {
      clientBuilder.setSSLContext(container.createSslContextFromCa());
      clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
      return clientBuilder;
    });
    // builder.setNodeSelector(INGEST_NODE_SELECTOR);

    final ObjectMapper mapper = new ObjectMapper();
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    var restClient = builder.build();
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(mapper));
    var client = new ElasticsearchClient(transport);
    var asyncClient = new ElasticsearchAsyncClient(transport);
  }
}
