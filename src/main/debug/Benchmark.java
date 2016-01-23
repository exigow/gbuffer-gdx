package main.debug;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Benchmark {

  private final static double NANOSECONDS_TO_MILISECONDS = 1_000_000;
  private static String ACTUAL_TASK;
  private static long START_TIME;
  private static Map<String, Time> TIMES = new LinkedHashMap<>();

  private static Time get(String name) {
    Time ref = TIMES.get(name);
    if (ref == null) {
      ref = new Time();
      TIMES.put(name, ref);
    }
    return ref;
  }

  public static void start(String name) {
    if (ACTUAL_TASK != null)
      throw new IllegalStateException("Task " + name + " not ended");
    ACTUAL_TASK = name;
    START_TIME = now();
  }

  public static void end() {
    if (ACTUAL_TASK == null)
      throw new IllegalStateException("No task in progress");
    get(ACTUAL_TASK).updateTime(now() - START_TIME);
    ACTUAL_TASK = null;
  }

  private static String formatTask(String key) {
    Time time = get(key);
    long averageTime = time.getAverageTime();
    return key + "(avg: " + toMs(averageTime) + ")";
  }

  private static String toMs(long val) {
    double asDoubleMs = (double) val / NANOSECONDS_TO_MILISECONDS;
    return String.format("%.2fms", asDoubleMs);
  }

  public static String generateRaportAndReset() {
    return TIMES.keySet().stream().map(Benchmark::formatTask).collect(Collectors.joining(" -> "));
  }

  private static long now() {
    return System.nanoTime();
  }

  private static class Time {

    private static int MAX_AVERAGE_SAMPLES = 16;
    private long time;
    private long prevTimes[] = new long[MAX_AVERAGE_SAMPLES];
    private int pointer = 0;

    public void updateTime(long to) {
      prevTimes[pointer++] = time;
      if (pointer >= MAX_AVERAGE_SAMPLES)
        pointer = 0;
      time = to;
    }

    public long getAverageTime() {
      long result = 0;
      for (long prevTime : prevTimes)
        result += prevTime;
      return result / MAX_AVERAGE_SAMPLES;
    }

  }

}
