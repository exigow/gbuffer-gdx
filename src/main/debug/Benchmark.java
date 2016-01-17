package main.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Benchmark {

  private final static double NANOSECONDS_TO_MILISECONDS = 1_000_000;
  private final static List<Task> TASKS = new ArrayList<>();
  private static Task ACTUAL_TASK;
  private static long startTime;

  public static void start(String description) {
    if (ACTUAL_TASK != null)
      throw new IllegalStateException("Task " + ACTUAL_TASK.description + " not ended");
    ACTUAL_TASK = new Task();
    ACTUAL_TASK.description = description;
    startTime = now();
  }

  public static void end() {
    if (ACTUAL_TASK == null)
      throw new IllegalStateException("No task in progress");
    ACTUAL_TASK.time = now() - startTime;
    TASKS.add(ACTUAL_TASK);
    ACTUAL_TASK = null;
  }

  private static String formatTask(Task task) {
    double executionTime = (double) task.time / NANOSECONDS_TO_MILISECONDS;
    return task.description + " " + String.format("%.2fms", executionTime);
  }

  public static String generateRaportAndReset() {
    Task total = new Task();
    total.description = "total";
    total.time = calcTotalTime();
    TASKS.add(total);
    String report = TASKS.stream().map(Benchmark::formatTask).collect(Collectors.joining(" -> "));
    TASKS.clear();
    return report;
  }

  private static long calcTotalTime() {
    long result = 0;
    for (Task t : TASKS)
      result += t.time;
    return result;
  }

  private static long now() {
    return System.nanoTime();
  }

  private static class Task {

    public String description;
    public long time;

  }


}
