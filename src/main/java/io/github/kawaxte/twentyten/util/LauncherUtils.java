package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.EPlatform;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import java.awt.Container;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarFile;
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

  public static final Pattern JWT_PATTERN;
  public static final Pattern UUID_PATTERN;
  public static final Path WORKING_DIR_PATH;
  static final Logger LOGGER;
  public static Boolean outdated;
  private static URL githubReleasesUrl;

  static {
    JWT_PATTERN =
        Pattern.compile("^[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?$");
    UUID_PATTERN =
        Pattern.compile(
            "^[A-Fa-f0-9]{8}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{12}?$");

    LOGGER = LogManager.getLogger(LauncherUtils.class);
    WORKING_DIR_PATH = getWorkingDir();

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
      LOGGER.error("Failed to create URL for GitHub API", ioe);
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
      LOGGER.warn("Could not create {}", workingDirFile);
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
    } catch (FileNotFoundException fnfe) {
      return "N/A";
    } catch (IOException ioe) {
      LOGGER.error("Failed to read manifest from {}", jarFileUrl, ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Failed to parse {} as URI", jarFileUrl, urise);
    } finally {
      if (jarFileUrl.getFile().endsWith(".jar")) {
        LOGGER.info("Retrieve '{}' from {}", name, jarFileUrl);
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
                        .execute()
                        .returnContent()
                        .asString(StandardCharsets.UTF_8);
                val body = new JSONArray(request);
                val tagName = body.getJSONObject(0).getString("tag_name");

                val buildTime = getManifestAttribute("Build-Time");
                return Objects.compare(buildTime, tagName, String::compareTo) < 0;
              } catch (IOException ioe) {
                LOGGER.error("Failed to check for updates", ioe);
              } catch (URISyntaxException urise) {
                LOGGER.error("Failed to parse {} as URI", githubReleasesUrl, urise);
              }
              return false;
            }
          };
      worker.execute();

      try {
        outdated = worker.get();
      } catch (InterruptedException ie) {
        LOGGER.error("Interrupted while checking for updates", ie);
      } catch (ExecutionException ee) {
        LOGGER.error("Error while checking for updates", ee.getCause());
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
      LauncherUtils.LOGGER.error("Failed to browse {}", url, ioe);
    } catch (URISyntaxException urise) {
      LauncherUtils.LOGGER.error("Failed to parse {} as URI", url, urise);
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
