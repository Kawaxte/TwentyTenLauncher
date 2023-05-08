package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.EPlatform;
import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.ui.JGroupBox;
import java.awt.Container;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import lombok.val;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

public final class LauncherUtils {

  private static final Logger LOGGER;
  public static Pattern jwtPattern;
  public static Pattern uuidPattern;
  public static Path workingDirectoryPath;
  public static Boolean outdated;
  public static URL signupUrl;
  public static URL releasesUrl;

  static {
    LOGGER = LogManager.getLogger(LauncherUtils.class);

    jwtPattern = Pattern.compile("^[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?$");
    uuidPattern =
        Pattern.compile(
            "^[A-Fa-f0-9]{8}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{12}?$");
    workingDirectoryPath = getWorkingDirectoryPath();
    outdated = null;

    try {
      signupUrl =
          new URL(
              new StringBuilder()
                  .append("https://signup.live.com/")
                  .append("signup")
                  .append("?client_id=000000004420578E")
                  .append("&cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d")
                  .append("&lic=1")
                  .append("&uaid=e6e4ffd0ad4943ab9bf740fb4a0416f9")
                  .append("&wa=wsignin1.0")
                  .toString());
      releasesUrl =
          new URL(
              new StringBuilder()
                  .append("https://api.github.com/")
                  .append("repos/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("releases")
                  .toString());
    } catch (IOException ioe) {
      LOGGER.error("Failed to create URL", ioe);
    }
  }

  private LauncherUtils() {}

  public static Path getWorkingDirectoryPath() {
    val userHome = System.getProperty("user.home", ".");
    val appData = System.getenv("APPDATA");

    val workingDirectoryLookup =
        Collections.unmodifiableMap(
            new HashMap<EPlatform, Path>() {
              {
                put(EPlatform.LINUX, Paths.get(userHome, ".twentyten"));
                put(
                    EPlatform.MACOS,
                    Paths.get(userHome, "Library", "Application Support", "twentyten"));
                put(EPlatform.WINDOWS, Paths.get(appData, ".twentyten"));
              }
            });

    val workingDirectory = workingDirectoryLookup.get(EPlatform.getPlatform()).toFile();
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", workingDirectory.getAbsolutePath());
      return null;
    }
    return workingDirectory.toPath();
  }

  public static boolean isOutdated() {
    if (outdated == null) {
      val worker =
          new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
              try {
                val request =
                    Request.get(releasesUrl.toURI())
                        .addHeader("accept", "application/vnd.github+json")
                        .addHeader("X-GitHub-Api-Version", "2022-11-28")
                        .execute()
                        .returnContent()
                        .asString(StandardCharsets.UTF_8);
                val body = new JSONArray(request);
                val tagName = body.getJSONObject(0).getString("tag_name");

                val buildTime = JarUtils.getManifestAttribute("Build-Time");
                return Objects.compare(buildTime, tagName, String::compareTo) < 0;
              } catch (IOException ioe) {
                LOGGER.error("Failed to check for updates", ioe);
              } catch (URISyntaxException urise) {
                LOGGER.error("Failed to parse {} as URI", releasesUrl, urise);
              }
              return false;
            }
          };
      worker.execute();

      try {
        outdated = worker.get();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();

        LOGGER.error("Interrupted while checking for updates", ie);
      } catch (ExecutionException ee) {
        LOGGER.error("Error while checking for updates", ee.getCause());
      } finally {
        worker.cancel(true);
      }
    }
    return outdated;
  }

  public static void addComponentToContainer(Container c1, JComponent c2) {
    if (SwingUtilities.isEventDispatchThread()) {
      c1.removeAll();
      c1.add(c2);
      c1.revalidate();
      c1.repaint();
    } else {
      SwingUtilities.invokeLater(
          () -> {
            c1.removeAll();
            c1.add(c2);
            c1.revalidate();
            c1.repaint();
          });
    }
  }

  public static void updateContainerKeyValue(
      UTF8ResourceBundle bundle, Container c, String key, Object... args) {
    if (c instanceof JFrame) {
      val frame = (JFrame) c;
      frame.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JDialog) {
      val dialog = (JDialog) c;
      dialog.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
  }

  public static void updateComponentKeyValue(
      UTF8ResourceBundle bundle, JComponent c, String key, Object... args) {
    if (c instanceof AbstractButton) {
      val button = (AbstractButton) c;
      button.setText(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JGroupBox) {
      val groupBox = (JGroupBox) c;
      groupBox.setTitledBorder(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JLabel) {
      val label = (JLabel) c;
      label.setText(MessageFormat.format(bundle.getString(key), args));
    }
  }

  public static void openBrowser(String url) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI(url));
      }
    } catch (IOException ioe) {
      LauncherUtils.LOGGER.error("Failed to browse {}", url, ioe);
    } catch (URISyntaxException urise) {
      LauncherUtils.LOGGER.error("Failed to parse {} as URI", url, urise);
    } finally {
      LOGGER.info("Opened {} in browser", url);
    }
  }

  public static void openDesktop(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(p.toFile());
      }
    } catch (IOException ioe) {
      LauncherUtils.LOGGER.error("Failed to open {}", p, ioe);
    }
  }
}
