package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.EPlatform;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import java.awt.Container;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarFile;
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

  public static final Path WORKING_DIR_PATH;
  public static Boolean outdated;
  static Logger logger;
  private static URL githubReleasesUrl;

  static {
    WORKING_DIR_PATH = getWorkingDir();

    logger = LogManager.getLogger(LauncherUtils.class);

    try {
      githubReleasesUrl =
          new URL(
              new StringBuilder()
                  .append("https://api.github.com/")
                  .append("repos/")
                  .append("Kawaxte/")
                  .append("TwentyTenLauncher/")
                  .append("releases")
                  .toString());
    } catch (IOException ioe) {
      logger.error("Failed to create URL for GitHub API", ioe);
    }
    outdated = null;
  }

  private LauncherUtils() {}

  public static Path getWorkingDir() {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    val workingDirLookup =
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

    val workingDirFile = workingDirLookup.get(EPlatform.getPlatform()).toFile();
    if (!workingDirFile.exists() && !workingDirFile.mkdirs()) {
      logger.warn("Directory '{}' could not be created", workingDirFile);
      return null;
    }
    return workingDirFile.toPath();
  }

  public static String getManifestAttribute(String name) {
    val jarFileUrl =
        Optional.ofNullable(LauncherUtils.class.getProtectionDomain().getCodeSource().getLocation())
            .orElseThrow(() -> new RuntimeException("Failed to get code source location"));
    try (val jarFile = new JarFile(new File(jarFileUrl.toURI()))) {
      val manifest = jarFile.getManifest();
      val attributes = manifest.getMainAttributes();
      return attributes.getValue(name);
    } catch (IOException ioe) {
      logger.error("Failed to read manifest from {}", jarFileUrl, ioe);
    } catch (URISyntaxException urise) {
      logger.error("Failed to parse {} as URI", jarFileUrl, urise);
    } finally {
      if (jarFileUrl.getFile().endsWith(".jar")) {
        logger.info("Retrieving '{}' from {}", name, jarFileUrl);
      }
    }
    return null;
  }

  public static boolean isOutdated() {
    if (outdated == null) {
      val worker =
          new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
              try {
                val request =
                    Request.get(githubReleasesUrl.toURI())
                        .addHeader("accept", "application/vnd.github+json")
                        .addHeader("X-GitHub-Api-Version", "2022-11-28")
                        .execute();
                val response = request.returnContent();
                val body = new JSONArray(response.asString());
                val tagName = body.getJSONObject(0).getString("tag_name");

                val buildTime = getManifestAttribute("Build-Time");
                return Objects.compare(buildTime, tagName, String::compareTo) < 0;
              } catch (IOException ioe) {
                logger.error("Failed to check for updates", ioe);
              } catch (URISyntaxException urise) {
                logger.error("Failed to parse {} as URI", githubReleasesUrl, urise);
              }
              return false;
            }
          };
      worker.execute();

      try {
        outdated = worker.get();
      } catch (ExecutionException ee) {
        logger.error("Exception while checking for updates", ee);
        outdated = false;
      } catch (InterruptedException ie) {
        logger.error("Interrupted while checking for updates", ie);
        outdated = false;
      } finally {
        worker.cancel(true);
      }
    }
    return outdated;
  }

  public static void addPanel(Container c1, JComponent c2) {
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

  public static void openBrowser(URL url) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(url.toURI());
      }
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to browse {}", url, ioe);
    } catch (URISyntaxException urise) {
      LauncherUtils.logger.error("Failed to parse {} as URI", url, urise);
    }
  }

  public static void openDesktop(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(p.toFile());
      }
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to open {}", p, ioe);
    }
  }
}
