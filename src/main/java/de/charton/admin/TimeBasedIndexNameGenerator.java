package de.charton.admin;

import co.elastic.clients.util.VisibleForTesting;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
class TimeBasedIndexNameGenerator {

  private final Clock clock;

  TimeBasedIndexNameGenerator() {
    this(Clock.systemDefaultZone());
  }

  @VisibleForTesting
  TimeBasedIndexNameGenerator(Clock clock) {
    this.clock = clock;
  }

  String generateIndexName(String indexPrefix) {
    return indexPrefix + "-" + currentDateTimeAsString();
  }

  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu.MM.dd-HH.mm")
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.systemDefault());

  private String currentDateTimeAsString() {
    // current date time logic
    return dtf.format(clock.instant());
  }

  public static void main(String[] args) {
    System.out.println(new TimeBasedIndexNameGenerator().generateIndexName("test"));
  }
}
