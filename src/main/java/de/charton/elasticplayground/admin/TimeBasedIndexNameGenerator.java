package de.charton.elasticplayground.admin;

import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TimeBasedIndexNameGenerator {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu.MM.dd-HH.mm")
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.of("UTC"));

  private final Clock clock;

  TimeBasedIndexNameGenerator() {
    this(Clock.systemUTC());
  }

  String generateIndexName(String indexPrefix) {
    return indexPrefix + "-" + currentDateTimeAsString();
  }

  private String currentDateTimeAsString() {
    return FORMATTER.format(clock.instant());
  }

}
