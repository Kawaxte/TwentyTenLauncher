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

package ch.kawaxte.launcher.util;

import ch.kawaxte.launcher.EPlatform;
import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.impl.swing.JGroupBox;
import ch.kawaxte.launcher.ui.LauncherNoNetworkPanel;
import ch.kawaxte.launcher.ui.LauncherPanel;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import java.awt.Container;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class used for various purposes related to the inner workings of the launcher.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.3.2823_02
 */
public final class LauncherUtils {

  public static final Pattern JWT_PATTERN;
  public static final Pattern UUID_PATTERN;
  public static final Path WORKING_DIRECTORY_PATH;
  private static final Logger LOGGER;
  @Getter @Setter private static boolean notPremium;
  @Getter private static Boolean outdated;

  static {
    LOGGER = LoggerFactory.getLogger(LauncherUtils.class);

    JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+?" + "\\.[A-Za-z0-9-_]+$");
    UUID_PATTERN =
        Pattern.compile(
            "^[A-Fa-f0-9]{8}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{4}?"
                + "[A-Fa-f0-9]{12}$");

    WORKING_DIRECTORY_PATH = getWorkingDirectoryPath();
    notPremium = false;
    outdated = null;
  }

  private LauncherUtils() {}

  /**
   * Decodes a value from Base64.
   *
   * @param index the index of the value to decode
   * @return the decoded value if the length is not 0, otherwise an empty string
   */
  public static String decodeFromBase64(int index) {
    String value = LauncherConfig.get(index).toString();
    Objects.requireNonNull(value, "value cannot be null");

    byte[] bytes = (LauncherConfig.get(index).toString()).getBytes(StandardCharsets.UTF_8);
    return bytes.length == 0 ? "" : new String(Base64.getDecoder().decode(bytes));
  }

  /**
   * Encodes a value to Base64 as long as the value is not null or empty.
   *
   * @param value the value to encode
   * @return the encoded value if the length is not 0, otherwise {@code null}
   */
  public static String encodeToBase64(String value) {
    Objects.requireNonNull(value, "value cannot be null");
    if (value.isEmpty()) {
      throw new IllegalArgumentException("value cannot be empty");
    }

    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    return bytes.length == 0 ? "" : Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * Gets the URLs to either the Microsoft account sign-up page or the GitHub releases page.
   *
   * @return the URLs to either pages as an array
   */
  public static GenericUrl[] getGenericUrls() {
    GenericUrl[] urls = new GenericUrl[3];
    urls[0] =
        new GenericUrl(
            new StringBuilder()
                .append("https://signup.live.com/")
                .append("signup")
                .append("?client_id=000000004420578E")
                .append("&cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d")
                .append("&lic=1")
                .append("&uaid=e6e4ffd0ad4943ab9bf740fb4a0416f9")
                .append("&wa=wsignin1.0")
                .toString());
    urls[1] =
        new GenericUrl(
            new StringBuilder()
                .append("https://api.github.com/")
                .append("repos/")
                .append("Kawaxte/")
                .append("TwentyTenLauncher/")
                .append("releases")
                .toString());
    urls[2] =
        new GenericUrl(
            new StringBuilder()
                .append("https://github.com/")
                .append("Kawaxte/")
                .append("TwentyTenLauncher/")
                .append("releases/")
                .append("latest")
                .toString());
    return urls;
  }

  /**
   * Retrieves the Betacraft proxy host and proxy port used for fixing skins and sounds on legacy
   * versions of Minecraft.
   *
   * @return the Betacraft proxy host and proxy port as an array of strings
   */
  public static String[] getProxyHostAndPort() {
    String selectedVersion = (String) LauncherConfig.get(4);

    String fileName = "assets/versions.json";
    URL fileUrl = LauncherUtils.class.getClassLoader().getResource(fileName);

    InputStream is =
        Optional.ofNullable(LauncherUtils.class.getClassLoader().getResourceAsStream(fileName))
            .orElseThrow(() -> new NullPointerException("is cannot be null"));
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      JSONObject json = new JSONObject(br.lines().collect(Collectors.joining()));

      JSONArray versionArray;
      if (selectedVersion.startsWith("inf")) {
        versionArray = json.getJSONArray("legacy_infdev");
      } else if (selectedVersion.startsWith("a")) {
        versionArray = json.getJSONArray("legacy_alpha");
      } else if (selectedVersion.startsWith("b")) {
        versionArray = json.getJSONArray("legacy_beta");
      } else {
        versionArray = json.getJSONArray("legacy_release");
      }

      if (Objects.nonNull(versionArray)) {
        for (int i = 0; i < versionArray.length(); i++) {
          JSONObject version = versionArray.getJSONObject(i);
          String versionId = version.getString("versionId");
          if (Objects.equals(versionId, selectedVersion)) {
            String proxyHost = version.getString("bcProxyHost");
            int proxyPort = version.getInt("bcProxyPort");
            return new String[] {proxyHost, String.valueOf(proxyPort)};
          }
        }
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot read {}", fileUrl, ioe);
    }
    return new String[] {null, null};
  }

  /**
   * Returns the working directory path of the launcher based on platform.
   *
   * <p>To make it easier to understand, this is the equivalent to Microsoft's ".minecraft"
   * directory.
   *
   * @return the working directory path
   * @see <a href="https://minecraft.gamepedia.com/.minecraft">.minecraft</a>
   */
  public static Path getWorkingDirectoryPath() {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    Map<EPlatform, Path> directoryMap = new EnumMap<>(EPlatform.class);
    directoryMap.put(EPlatform.LINUX, Paths.get(userHome, ".twentyten"));
    directoryMap.put(
        EPlatform.MACOS, Paths.get(userHome, "Library", "Application Support", "twentyten"));
    directoryMap.put(EPlatform.WINDOWS, Paths.get(appData, ".twentyten"));

    File workingDir = directoryMap.get(EPlatform.getOSName()).toFile();
    if (!workingDir.exists() && !workingDir.mkdirs()) {
      LOGGER.warn("Could not create {}", workingDir.getAbsolutePath());
      return null;
    }
    return workingDir.toPath();
  }

  /**
   * Checks if the current version of the launcher is outdated.
   *
   * <p>It checks the current version of the launcher against the latest release on GitHub. The
   * version itself uses a custom format, which is as follows:
   *
   * <p>{@code 1.mm.ddyy} where {@code 1} is the major version that increments every new year,
   * {@code mm} is the month without leading zeros, {@code dd} is the day with leading zeros, and
   * {@code yy} is the year with leading zeros.
   *
   * <p>If we're working with a development build, the version will append {@code _##} at the end,
   * which is a value with a range of {@code 01-99} that increments every time a new pre-release
   * build is published on GitHub. and will reset whenever the month increments.
   *
   * @return {@code true} if the current version of the launcher is outdated, {@code false}
   * @see <a href="https://github.com/Kawaxte/TwentyTenLauncher/releases/latest">Latest Release</a>
   */
  public static boolean isOutdated() {
    if (Objects.isNull(outdated)) {
      SwingWorker<Boolean, Void> worker =
          new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
              HttpTransport transport = new NetHttpTransport();

              HttpRequestFactory factory = transport.createRequestFactory();
              try {
                HttpRequest request =
                    factory.buildGetRequest(
                        getGenericUrls()[1]
                            .set("accept", "application/vnd.github+json")
                            .set("X-GitHub-Api-Version", "2022-11-28"));
                HttpResponse response = request.execute();

                String body = response.parseAsString();
                JSONArray array = new JSONArray(body);
                String tagName = array.getJSONObject(0).getString("tag_name");
                String implVersion = this.getClass().getPackage().getImplementationVersion();
                if (Objects.isNull(implVersion)) {
                  implVersion = "1.99.9999_99"; // This can be used for testing purposes
                }
                return Objects.compare(implVersion, tagName, String::compareTo) < 0;
              } catch (UnknownHostException uhe) {
                LauncherUtils.swapContainers(
                    LauncherPanel.getInstance(),
                    new LauncherNoNetworkPanel(
                        LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
              } catch (IOException ioe) {
                LOGGER.error("Cannot check for updates", ioe);
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
        Throwable cause = ee.getCause();

        LOGGER.error("Error while checking for updates", cause);
      } finally {
        worker.cancel(true);
      }
    }
    return outdated;
  }

  /**
   * Swaps the specified containers, removing all components from the first container and adding the
   * second container to it.
   *
   * <p>If this method is called from the Event Dispatch thread (EDT), it will be executed
   * immediately. Otherwise, it will be executed in the Event Dispatch thread (EDT).
   *
   * @param c1 the first container
   * @param c2 the second container
   */
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

  /**
   * Sets the title of the specified container with the specified key and arguments.
   *
   * <p>This is used to dynamically update the title of containers that are not updated by the
   * {@link UTF8ResourceBundle} when the locale is changed.
   *
   * @param bundle the resource bundle to use
   * @param c the container to update
   * @param key the key to use
   * @param args the arguments to use
   */
  public static void setContainerTitle(
      UTF8ResourceBundle bundle, Container c, String key, Object... args) {
    if (c instanceof JFrame) {
      JFrame frame = (JFrame) c;
      frame.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JDialog) {
      JDialog dialog = (JDialog) c;
      dialog.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
  }

  /**
   * Updates the text of the specified component with the specified key and arguments.
   *
   * <p>This is used to dynamically update the text of components that are not updated by the {@link
   * UTF8ResourceBundle} when the locale is changed.
   *
   * @param bundle the resource bundle to use
   * @param c the component to update
   * @param key the key to use
   * @param args the arguments to use
   */
  public static void setComponentText(
      UTF8ResourceBundle bundle, JComponent c, String key, Object... args) {
    if (c instanceof AbstractButton) {
      AbstractButton button = (AbstractButton) c;
      button.setText(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JGroupBox) {
      JGroupBox groupBox = (JGroupBox) c;
      groupBox.setTitledBorder(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JLabel) {
      JLabel label = (JLabel) c;
      label.setText(MessageFormat.format(bundle.getString(key), args));
    }
  }

  /**
   * Opens the default browser to the specified URL as long as it is supported.
   *
   * @param url the URL to open in the system browser
   */
  public static void openBrowser(String url) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI(url));
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot browse {}", url, ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Cannot parse {} as URI", url, urise);
    }
  }

  /**
   * Opens the default browser to the specified URL as long as it is supported.
   *
   * @param p the path to open in the system browser
   */
  public static void openDesktop(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(p.toFile());
      }
    } catch (IOException ioe) {
      LOGGER.error("Cannot open {}", p, ioe);
    }
  }
}
