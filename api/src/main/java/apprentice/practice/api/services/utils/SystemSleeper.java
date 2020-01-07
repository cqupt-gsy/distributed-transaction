package apprentice.practice.api.services.utils;

public final class SystemSleeper {

  private SystemSleeper() {
  }

  public static void sleepOneSecond() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
