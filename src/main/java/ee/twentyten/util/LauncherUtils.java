package ee.twentyten.util;

import ee.twentyten.EPlatform;
import ee.twentyten.Launcher;
import ee.twentyten.log.ELogger;
import ee.twentyten.request.ERequestHeader;
import ee.twentyten.request.ERequestMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.json.JSONObject;

public final class LauncherUtils {

  public static final long MIN_MEMORY = 536870912L;
  public static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();
  public static File workingDirectory;
  public static URL apiLatestReleaseUrl;
  public static URL registrationUrl;
  public static URL latestReleaseUrl;
  public static boolean isUpdateChecked;
  public static boolean isOutdated;
  private static Map<EPlatform, File> workingDirectories;

  static {
    LauncherUtils.workingDirectories = new HashMap<>();
    LauncherUtils.workingDirectory = LauncherUtils.getWorkingDirectory();

    try {
      LauncherUtils.apiLatestReleaseUrl = new URL(
          "https://api.github.com/repos/sojlabjoi/TwentyTenLauncher/releases/latest");
      LauncherUtils.registrationUrl = new URL(
          new StringBuilder().append("https://signup.live.com/").append("signup?")
              .append("cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d")
              .append("&client_id=00000000402b5328").append("&lic=1").toString());
      LauncherUtils.latestReleaseUrl = new URL(
          "https://github.com/sojlabjoi/TwentyTenLauncher/releases/latest");
    } catch (MalformedURLException murle) {
      LoggerUtils.log("Failed to create URL", murle, ELogger.ERROR);
    }
  }

  private LauncherUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static File getWorkingDirectory() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    LauncherUtils.mapWorkingDirectoryToPlatform(platform);

    File workingDirectory = LauncherUtils.workingDirectories.get(platform);
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LoggerUtils.log("Failed to create working directory", ELogger.ERROR);
      return null;
    }
    return workingDirectory;
  }

  public static boolean isLauncherOutdated() {
    if (!LauncherUtils.isUpdateChecked) {
      ThreadFactory updateFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread t = new Thread(r);
          t.setName(String.format("update-%s", t.getId()));
          t.setDaemon(true);
          return t;
        }
      };
      ExecutorService updateService = Executors.newSingleThreadExecutor(updateFactory);
      Future<Boolean> updateFuture = updateService.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() {
          JSONObject latestRelease = RequestUtils.performJsonRequest(
              LauncherUtils.apiLatestReleaseUrl, ERequestMethod.GET, ERequestHeader.JSON);
          Objects.requireNonNull(latestRelease, "latestRelease == null!");

          String latestVersion = latestRelease.getString("tag_name");
          String currentVersion = SystemUtils.launcherVersion;
          return !Objects.equals(latestVersion, currentVersion);
        }
      });
      try {
        LauncherUtils.isOutdated = updateFuture.get();
        LauncherUtils.isUpdateChecked = true;
      } catch (ExecutionException ee) {
        LoggerUtils.log("Execution while checking for updates", ee, ELogger.ERROR);
      } catch (InterruptedException ie) {
        LoggerUtils.log("Interrupted while checking for updates", ie, ELogger.ERROR);
      } finally {
        updateService.shutdown();
      }
    }
    return LauncherUtils.isOutdated;
  }

  public static void buildLowMemoryProcess() {
    EPlatform platform = EPlatform.getPlatform();

    List<String> arguments = new ArrayList<>();
    arguments.add(platform == EPlatform.WINDOWS ? "javaw" : "java");
    arguments.add("-Xmx1024m");
    arguments.add("-Xms512m");
    arguments.add("-Dsun.java2d.d3d=false");
    arguments.add("-Dsun.java2d.opengl=false");
    arguments.add("-Dsun.java2d.pmoffscreen=false");
    arguments.add("-cp");
    arguments.add(System.getProperty("java.class.path"));
    arguments.add(Launcher.class.getCanonicalName());

    ProcessBuilder pb = new ProcessBuilder(arguments);
    LoggerUtils.log(arguments.toString(), ELogger.INFO);
    try {
      Process p = pb.start();

      String errorStreamMessage = LauncherUtils.getErrorStream(p);
      if (!errorStreamMessage.isEmpty()) {
        LoggerUtils.log(errorStreamMessage, ELogger.ERROR);
      }
      System.exit(p.waitFor());
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to start process", ioe, ELogger.ERROR);
    } catch (InterruptedException ie) {
      LoggerUtils.log("Interrupted while waiting for process to finish", ie, ELogger.ERROR);
      System.exit(1);
    }
  }

  private static String getErrorStream(Process p) {
    StringBuilder sb = new StringBuilder();
    try (InputStreamReader isr = new InputStreamReader(
        p.getErrorStream()); BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append(SystemUtils.lineSeparator);
      }
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to read error stream from process", ioe, ELogger.ERROR);
    }
    return sb.toString();
  }

  private static void mapWorkingDirectoryToPlatform(EPlatform platform) {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    File workingDirectory;
    switch (platform) {
      case MACOSX:
        workingDirectory = new File(userHome, "Library/Application Support/minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      case LINUX:
        workingDirectory = new File(userHome, "minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      case WINDOWS:
        workingDirectory =
            appData != null ? new File(appData, ".minecraft") : new File(userHome, ".minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
  }
}
