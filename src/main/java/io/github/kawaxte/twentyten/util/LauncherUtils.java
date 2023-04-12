package io.github.kawaxte.twentyten.util;

import io.github.kawaxte.twentyten.Launcher.EPlatform;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import java.awt.Container;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LauncherUtils {

  public static final Path WORKING_DIR_PATH;
  static Logger logger;

  static {
    WORKING_DIR_PATH = getWorkingDir();

    logger = LogManager.getLogger(LauncherUtils.class);
  }

  private LauncherUtils() {
  }

  public static Path getWorkingDir() {
    String userHome = System.getProperty("user.home", ".");
    String appData = System.getenv("APPDATA");

    val workingDirLookup = Collections.unmodifiableMap(
        new HashMap<EPlatform, Path>() {
          {
            put(EPlatform.LINUX, Paths.get(
                userHome, ".twentyten"));
            put(EPlatform.MACOS, Paths.get(
                userHome, "Library", "Application Support", "twentyten"));
            put(EPlatform.WINDOWS, Paths.get(
                appData, ".twentyten"));
          }
        });

    val workingDirFile = workingDirLookup.get(EPlatform.getPlatform()).toFile();
    if (!workingDirFile.exists() && !workingDirFile.mkdirs()) {
      logger.warn("Directory '{}' could not be created", workingDirFile);
      return null;
    }
    return workingDirFile.toPath();
  }

  public static void addPanel(Container c1, JComponent c2) {
    if (SwingUtilities.isEventDispatchThread()) {
      c1.removeAll();
      c1.add(c2);
      c1.revalidate();
      c1.repaint();
    } else {
      SwingUtilities.invokeLater(() -> {
        c1.removeAll();
        c1.add(c2);
        c1.revalidate();
        c1.repaint();
      });
    }
  }

  public static void updateContainerKeyValue(UTF8ResourceBundle bundle, Container c, String key,
      Object... args) {
    if (c instanceof JFrame) {
      val frame = (JFrame) c;
      frame.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JDialog) {
      val dialog = (JDialog) c;
      dialog.setTitle(MessageFormat.format(bundle.getString(key), args));
    }
  }

  public static void updateComponentKeyValue(UTF8ResourceBundle bundle, JComponent c, String key,
      Object... args) {
    if (c instanceof AbstractButton) {
      var button = (AbstractButton) c;
      button.setText(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JGroupBox) {
      var groupBox = (JGroupBox) c;
      groupBox.setTitledBorder(MessageFormat.format(bundle.getString(key), args));
    }
    if (c instanceof JLabel) {
      var label = (JLabel) c;
      label.setText(MessageFormat.format(bundle.getString(key), args));
    }
  }

  public static void openBrowser(URL url) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(url.toURI());
      }
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to open '{}' in browser",
          url,
          ioe);
    } catch (URISyntaxException urise) {
      LauncherUtils.logger.error("Failed to parse '{}' as URI",
          url,
          urise);
    }
  }

  public static void openDesktop(Path p) {
    try {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(p.toFile());
      }
    } catch (IOException ioe) {
      LauncherUtils.logger.error("Failed to open '{}' in desktop",
          p,
          ioe);
    }
  }
}
