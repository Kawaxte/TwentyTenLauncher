package ee.twentyten.util;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public final class LauncherManager {

  public static final String LATEST_RELEASE_URL;
  public static final String ACCOUNT_SIGNUP_URL;
  private static final String LATEST_RELEASE_API_URL;
  public static String currentVersion = null;

  static {
    LATEST_RELEASE_URL = "https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest";
    ACCOUNT_SIGNUP_URL = "https://signup.live.com/signup?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d&client_id=00000000402b5328&lic=1";
    LATEST_RELEASE_API_URL = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";
  }

  private LauncherManager() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
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

    LauncherManager.currentVersion = String.format("%d.%s.%s%s", x, formattedMonth, formattedDay,
        formattedYear);
    return LauncherManager.currentVersion;
  }

  public static boolean isOutdated() {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        String latestVersion = RequestManager.requestJsonGet(LATEST_RELEASE_API_URL).get("tag_name")
            .toString();
        return !currentVersion.equals(latestVersion);
      }
    };
    worker.execute();

    try {
      return worker.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Failed to check for updates", e);
    } catch (ExecutionException e) {
      JOptionPane.showMessageDialog(null,
          String.format("An error occurred while checking for updates:%n%s", e.getMessage()),
          "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
  }
}
