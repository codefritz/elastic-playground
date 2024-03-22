package de.charton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
class TimeBasedIndexNameGenerator {

  String generateIndexName(String indexPrefix) {
    return indexPrefix + "-" + currentDateTimeAsString();
  }

  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu.MM.dd-HH.mm");

  private String currentDateTimeAsString() {
    // current date time logic
    return dtf.format(LocalDateTime.now());
  }

  public static void main(String[] args) {
    System.out.println(new TimeBasedIndexNameGenerator().generateIndexName("test"));
  }
}
