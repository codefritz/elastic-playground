package de.charton.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

class TimeBasedIndexNameGeneratorTest {

      @org.junit.jupiter.api.Test
      void generateIndexName() {
        Clock clock = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), ZoneId.systemDefault());
        TimeBasedIndexNameGenerator timeBasedIndexNameGenerator = new TimeBasedIndexNameGenerator(clock);

        String indexName = timeBasedIndexNameGenerator.generateIndexName("test");

        assertThat(indexName).isEqualTo("test-2021.01.01-01.00");
      }
}