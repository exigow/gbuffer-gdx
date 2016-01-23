package main.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {

  private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

  public static void log(String log) {
    String time = LocalDateTime.now().format(FORMATTER);
    System.out.println(fixedLengthString(time, 12) + ": " + log);
  }

  private static String fixedLengthString(String s, int n) {
    return String.format("%1$-" + n + "s", s);
  }

}
