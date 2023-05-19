/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.EPlatform;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.custom.JGroupBox;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import org.json.JSONObject;

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
      LOGGER.error("Cannot create URL(s)", ioe);
    }
  }

  private LauncherUtils() {}

  public static String[] getProxyHostAndPort() {
    val selectedVersion = (String) LauncherConfig.lookup.get("selectedVersion");
    val fileUrl =
        Optional.ofNullable(
                LauncherOptionsUtils.class.getClassLoader().getResource("versions.json"))
            .orElseThrow(() -> new NullPointerException("fileUrl cannot be null"));
    try (val br =
        new BufferedReader(new InputStreamReader(fileUrl.openStream(), StandardCharsets.UTF_8))) {
      val json = new JSONObject(br.lines().collect(Collectors.joining()));

      JSONArray versionArray = null;
      if (selectedVersion.startsWith("b")) {
        versionArray = json.getJSONArray("legacy_beta");
      }
      if (selectedVersion.startsWith("a")) {
        versionArray = json.getJSONArray("legacy_alpha");
      }
      if (selectedVersion.startsWith("inf")) {
        versionArray = json.getJSONArray("legacy_infdev");
      }

      if (Objects.nonNull(versionArray)) {
        for (int i = 0; i < versionArray.length(); i++) {
          val version = versionArray.getJSONObject(i);
          val versionId = version.getString("versionId");
          if (Objects.equals(versionId, selectedVersion)) {
            val proxyHost = version.getString("bcProxyHost");
            val proxyPort = version.getInt("bcProxyPort");
            return new String[] {proxyHost, String.valueOf(proxyPort)};
          }
        }
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot read {}", fileUrl, ioe);
    }
    return null;
  }

  public static String getManifestAttribute(String key) {
    val fileUrl =
        Optional.ofNullable(LauncherUtils.class.getProtectionDomain().getCodeSource().getLocation())
            .orElseThrow(() -> new NullPointerException("fileUrl cannot be null"));
    try (val file = new JarFile(new File(fileUrl.toURI()))) {
      val manifest = file.getManifest();
      val attributes = manifest.getMainAttributes();
      return attributes.getValue(key);
    } catch (FileNotFoundException fnfe) {
      val currentInstant = Instant.now();
      return new SimpleDateFormat("1.M.ddyy").format(currentInstant.toEpochMilli());
    } catch (IOException ioe) {
      LOGGER.error("Cannot retrieve '{}' from {}", key, fileUrl, ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Cannot parse {} as URI", fileUrl, urise);
    }
    return null;
  }

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

    val workingDirectory = workingDirectoryLookup.get(EPlatform.getOSName()).toFile();
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
      LOGGER.warn("Could not create {}", workingDirectory.getAbsolutePath());
      return null;
    }
    return workingDirectory.toPath();
  }

  public static boolean isOutdated() {
    if (Objects.isNull(outdated)) {
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

                val buildTime = getManifestAttribute("Build-Time");
                return Objects.compare(buildTime, tagName, String::compareTo) < 0;
              } catch (UnknownHostException uhe) {
                LauncherUtils.swapContainers(
                    LauncherPanel.instance,
                    new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", uhe.getMessage()));
              } catch (IOException ioe) {
                LOGGER.error("Cannot check for updates", ioe);
              } catch (URISyntaxException urise) {
                LOGGER.error("Cannot convert {} to URI", releasesUrl, urise);
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
        val cause = ee.getCause();

        LOGGER.error("Error while checking for updates", cause);
      } finally {
        worker.cancel(true);
      }
    }
    return outdated;
  }

  public static void swapContainers(Container c1, Container c2) {
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
      LauncherUtils.LOGGER.error("Cannot browse {}", url, ioe);
    } catch (URISyntaxException urise) {
      LauncherUtils.LOGGER.error("Cannot convert {} to URI", url, urise);
    }
  }

  public static void openDesktop(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(p.toFile());
      }
    } catch (IOException ioe) {
      LauncherUtils.LOGGER.error("Cannot open {}", p, ioe);
    }
  }
}
