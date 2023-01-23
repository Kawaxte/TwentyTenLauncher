package ee.twentyten.utils;

import java.util.Calendar;

public final class VersionManager {

  private static final String API_GITHUB_LATEST = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";

  public static String currentVersion = null;

  private VersionManager() {
  }

  public static String getCurrentVersion() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int x = (year - 2022) % 10;

    String formattedYear = String.format("%02d", year % 100);
    String formattedDay = String.format("%02d", day);
    String formattedMonth = String.format("%02d", month);

    currentVersion = String.format("%d.%s.%s%s", x, formattedMonth, formattedDay, formattedYear);
    return currentVersion;
  }
}
