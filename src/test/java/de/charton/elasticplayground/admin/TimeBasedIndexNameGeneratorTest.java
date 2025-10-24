package de.charton.elasticplayground.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class TimeBasedIndexNameGeneratorTest {

    @Test
    void generateIndexName_WhenTimeStampGiven_ShouldUsedAsIndexSuffix() {
      Clock clock = Clock.fixed(Instant.parse("2024-09-21T05:45:45Z"), ZoneId.of("GMT"));
      TimeBasedIndexNameGenerator timeBasedIndexNameGenerator = new TimeBasedIndexNameGenerator(clock);

      String indexName = timeBasedIndexNameGenerator.generateIndexName("test");

      assertThat(indexName).isEqualTo("test-2024.09.21-07.45");
    }

}