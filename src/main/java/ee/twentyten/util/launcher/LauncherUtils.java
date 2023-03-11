package ee.twentyten.util.launcher;

import ee.twentyten.EPlatform;
import ee.twentyten.Launcher;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.launcher.options.LanguageUtils;
import ee.twentyten.util.log.LoggerUtils;
import ee.twentyten.util.request.ConnectionRequestUtils;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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

    LauncherUtils.mapWorkingDirectoryToPlatforms(platform);

    File workingDirectory = LauncherUtils.workingDirectories.get(platform);
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LoggerUtils.logMessage("Failed to create working directory", ELevel.ERROR);
      return null;
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

  private static void setGlobalArguments(EPlatform platform, List<String> args) {
    switch (platform) {
      case MACOSX:
      case LINUX:
      case WINDOWS:
        args.add("-Dsun.java2d.opengl=false");
        break;
      default:
        break;
    }
  }

  private static void setPlatformSpecificArguments(EPlatform platform, List<String> args) {
    switch (platform) {
      case LINUX:
        args.add("-Dsun.java2d.xrender=True");
        args.add("-Dsun.java2d.pmoffscreen=false");
        break;
      case WINDOWS:
        args.add("-Dsun.java2d.d3d=false");
        break;
      default:
        break;
    }
  }

  public static boolean isNetworkNotAvailable(final String hostName) {
    final boolean[] isNetworkNotAvailable = {true};
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        try {
          InetAddress address = InetAddress.getByName(hostName);
          return !address.isReachable(1000);
        } catch (UnknownHostException uhe) {
          LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
              new LauncherNoNetworkPanel(), MessageFormat.format(
                  LanguageUtils.getString(LanguageUtils.getBundle(),
                      "lp.label.errorLabel.noNetwork"), uhe.getMessage()));
        } catch (IOException ioe) {
          LoggerUtils.logMessage(MessageFormat.format("Can't connect to {0}", hostName), ioe,
              ELevel.ERROR);
        }
        return true;
      }

      @Override
      protected void done() {
        try {
          isNetworkNotAvailable[0] = !get(15, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
          LoggerUtils.logMessage("Failed to check for network availability", ee, ELevel.ERROR);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          LoggerUtils.logMessage("Interrupted while checking for network availability", ie,
              ELevel.ERROR);
        } catch (TimeoutException te) {
          LoggerUtils.logMessage("Timeout while checking for network availability", te,
              ELevel.ERROR);
        }
      }
    }.execute();
    return !isNetworkNotAvailable[0];
  }

  public static boolean isLauncherOutdated() {
    if (!LauncherUtils.isUpdateChecked) {
      ExecutorService updateService = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread t = new Thread(r);
          t.setName(MessageFormat.format("outdated-{0}", t.getId()));
          t.setDaemon(true);
          return t;
        }
      });
      Future<Boolean> updateFuture = updateService.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() {
          JSONObject latestRelease = new ConnectionRequest.Builder()
              .setUrl(LauncherUtils.apiLatestReleaseUrl)
              .setMethod(EMethod.GET)
              .setHeaders(ConnectionRequestUtils.JSON)
              .setSSLSocketFactory(ConnectionRequestUtils.getSSLSocketFactory())
              .build().performJsonRequest();

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
        updateService.shutdownNow();
      }
    }
    return LauncherUtils.isOutdated;
  }

  public static void buildAndCreateProcess() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    List<String> arguments = new ArrayList<>();
    arguments.add(platform == EPlatform.WINDOWS ? "javaw" : "java");
    arguments.add("-Xmx1024m");
    arguments.add("-Xms512m");
    LauncherUtils.setGlobalArguments(platform, arguments);
    LauncherUtils.setPlatformSpecificArguments(platform, arguments);
    arguments.add("-cp");
    arguments.add(SystemUtils.javaClasspath);
    arguments.add(Launcher.class.getCanonicalName());
    LoggerUtils.logMessage(String.valueOf(arguments), ELevel.INFO);

    ProcessBuilder pb = new ProcessBuilder(arguments);
    pb.redirectErrorStream(true);
    try {
      Process p = pb.start();

      StringBuilder sb = new StringBuilder();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line).append(System.lineSeparator());
        }
      }
      LoggerUtils.logMessage(sb.toString(), ELevel.INFO);

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

  private static void mapWorkingDirectoryToPlatforms(EPlatform platform) {
    File workingDirectory;
    switch (platform) {
      case MACOSX:
        workingDirectory = new File(SystemUtils.userHome, "Library/Application Support/minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      case LINUX:
        workingDirectory = new File(SystemUtils.userHome, ".minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      case WINDOWS:
        workingDirectory = SystemUtils.appData != null ? new File(SystemUtils.appData, ".minecraft")
            : new File(SystemUtils.userHome, ".minecraft");
        LauncherUtils.workingDirectories.put(platform, workingDirectory);
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
  }
}
