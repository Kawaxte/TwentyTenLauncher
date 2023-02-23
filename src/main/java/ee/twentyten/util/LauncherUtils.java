package ee.twentyten.util;

import ee.twentyten.EPlatform;
import ee.twentyten.Launcher;
import ee.twentyten.config.LauncherConfigImpl;
import ee.twentyten.log.ELogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class LauncherUtils {

  public static final long MIN_MEMORY = 536870912L;
  public static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();
  public static File workingDirectory;
  private static Map<EPlatform, File> workingDirectories;
  private static LauncherConfigImpl config;

  static {
    LauncherUtils.workingDirectories = new HashMap<>();
    LauncherUtils.workingDirectory = LauncherUtils.getWorkingDirectory();

    LauncherUtils.config = new LauncherConfigImpl();
  }

  private LauncherUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static File getWorkingDirectory() {
    EPlatform platform = EPlatform.getCurrentPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    LauncherUtils.mapWorkingDirectoryToPlatform(platform);

    File workingDirectory = LauncherUtils.workingDirectories.get(platform);
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LoggerUtils.log("Failed to create working directory", ELogger.ERROR);
      return null;
    }
    return workingDirectory;
  }

  public static void buildLowMemoryProcess() {
    EPlatform platform = EPlatform.getCurrentPlatform();

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
    pb.redirectErrorStream(true);
    try {
      Process p = pb.start();
      LoggerUtils.log(String.format("Building process with arguments: %s", arguments),
          ELogger.INFO);

      System.exit(p.waitFor());
    } catch (IOException ioe) {
      LoggerUtils.log("Failed to start process", ioe, ELogger.ERROR);
    } catch (InterruptedException ie) {
      LoggerUtils.log("Interrupted while waiting for process to finish", ie, ELogger.ERROR);
      System.exit(1);
    }
  }

  public static void readFromConfig() {
    LauncherUtils.config.load();
  }

  public static void writeToConfig() {
    LauncherUtils.config.save();
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
