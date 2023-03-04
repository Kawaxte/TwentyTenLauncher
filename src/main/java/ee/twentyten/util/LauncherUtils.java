package ee.twentyten.util;

import ee.twentyten.EPlatform;
import ee.twentyten.Launcher;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
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
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
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
      LoggerUtils.logMessage("Failed to create working directory", ELevel.ERROR);
    }
    return workingDirectory;
  }

  public static void setContentPaneToContainer(JComponent c, Container oldCont, Container newCont) {
    oldCont.remove(c);

    if (oldCont instanceof JFrame) {
      ((JFrame) oldCont).setContentPane(newCont);
    }

    oldCont.revalidate();
    oldCont.repaint();
  }

  public static boolean isLauncherOutdated() {
    if (!LauncherUtils.isUpdateChecked) {
      ThreadFactory updateFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread updateThread = new Thread(r);
          updateThread.setName(MessageFormat.format("update-{0}", updateThread.getId()));
          updateThread.setDaemon(true);
          return updateThread;
        }
      };

      ExecutorService updateService = Executors.newSingleThreadExecutor(updateFactory);
      Future<Boolean> updateFuture = updateService.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() {
          JSONObject latestRelease = RequestUtils.performJsonRequest(
              LauncherUtils.apiLatestReleaseUrl, EMethod.GET, EHeader.JSON.getHeader());
          Objects.requireNonNull(latestRelease, "latestRelease == null!");

          String latestVersion = latestRelease.getString("tag_name");
          String currentVersion = SystemUtils.launcherVersion;
          return currentVersion.compareTo(latestVersion) < 0;
        }
      });
      try {
        LauncherUtils.isOutdated = updateFuture.get();
        LauncherUtils.isUpdateChecked = true;
      } catch (ExecutionException ee) {
        LoggerUtils.logMessage("Failed to check for updates", ee, ELevel.ERROR);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        LoggerUtils.logMessage("Interrupted while checking for updates", ie, ELevel.ERROR);
      } finally {
        updateService.shutdown();
      }
    }
    return LauncherUtils.isOutdated;
  }

  public static void buildAndCreateProcess() {
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
    LoggerUtils.logMessage(arguments.toString(), ELevel.INFO);
    try {
      Process p = pb.start();

      String errorStreamMessage = LauncherUtils.getErrorStream(p);
      if (!errorStreamMessage.isEmpty()) {
        LoggerUtils.logMessage(errorStreamMessage, ELevel.ERROR);
      }
      System.exit(p.waitFor());
    } catch (IOException ioe) {
      LoggerUtils.logMessage("Failed to start process", ioe, ELevel.ERROR);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      LoggerUtils.logMessage("Interrupted while waiting for process to finish", ie, ELevel.ERROR);
      System.exit(1);
    }
  }

  public static void addPanel(final JComponent oldComp, final JComponent newComp) {
    if (SwingUtilities.isEventDispatchThread()) {
      oldComp.removeAll();
      oldComp.add(newComp);
      oldComp.revalidate();
      oldComp.repaint();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          LauncherUtils.addPanel(oldComp, newComp);
        }
      });
    }
  }

  public static void addPanelWithErrorMessage(final JComponent oldComp, final JComponent newComp,
      final String message) {
    if (SwingUtilities.isEventDispatchThread()) {
      oldComp.removeAll();
      oldComp.add(newComp);
      if (newComp instanceof LauncherNoNetworkPanel) {
        LauncherNoNetworkPanel.getInstance().getErrorLabel().setText(message);
      }
      oldComp.revalidate();
      oldComp.repaint();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          LauncherUtils.addPanelWithErrorMessage(oldComp, newComp, message);
        }
      });
    }
  }

  public static void openDesktop(File directory) {
    boolean isDesktopSupported = Desktop.isDesktopSupported();
    if (isDesktopSupported) {
      try {
        Desktop d = Desktop.getDesktop();
        if (d.isSupported(Desktop.Action.OPEN)) {
          d.open(directory);
        }
      } catch (IOException ioe) {
        LoggerUtils.logMessage("Failed to open directory", ioe, ELevel.ERROR);
      }
    }
  }

  public static void browseDesktop(URL url) {
    boolean isDesktopSupported = Desktop.isDesktopSupported();
    if (isDesktopSupported) {
      try {
        Desktop d = Desktop.getDesktop();
        if (d.isSupported(Desktop.Action.BROWSE)) {
          d.browse(url.toURI());
        }
      } catch (IOException ioe) {
        LoggerUtils.logMessage("Failed to open browser", ioe, ELevel.ERROR);
      } catch (URISyntaxException urise) {
        LoggerUtils.logMessage("Failed to resolve URI", urise, ELevel.ERROR);
      }
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
      LoggerUtils.logMessage("Failed to read error stream from process", ioe, ELevel.ERROR);
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
