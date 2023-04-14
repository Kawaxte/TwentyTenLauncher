package io.github.kawaxte.twentyten;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.conf.LauncherConfig;
import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.ui.LauncherFrame;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class Launcher {

  private Launcher() {
  }

  static Logger logger;

  static {
    logger = LogManager.getLogger(Launcher.class);
  }

  public static void main(String... args) {
    LauncherConfig.loadConfig();

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    LauncherLanguage.loadLanguage("messages",
        Objects.nonNull(selectedLanguage)
            && !selectedLanguage.isEmpty()
            ? selectedLanguage
            : System.getProperty("user.language"));

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ReflectiveOperationException roe) {
      logger.error("Failed to set look and feel",
          roe);
    } catch (UnsupportedLookAndFeelException ulafe) {
      logger.error("'{}' is not supported on '{}'",
          UIManager.getLookAndFeel().getName(),
          System.getProperty("os.name"),
          ulafe);
    } finally {
      logger.info("Setting look and feel to '{}'",
          UIManager.getLookAndFeel().getName());
    }

    SwingUtilities.invokeLater(() -> {
      val launcherFrame = new LauncherFrame();
      launcherFrame.setVisible(true);
    });
  }

  public enum EPlatform {
    LINUX("aix,", "nix", "nux"),
    MACOS("darwin", "mac"),
    WINDOWS("win");

    private static final String OS_NAME = System.getProperty("os.name");
    private final List<String> osNames;

    EPlatform(String... osNames) {
      this.osNames = Collections.unmodifiableList(Arrays.asList(osNames));
    }

    public static EPlatform getPlatform() {
      return Arrays.stream(values())
          .filter(platform -> platform.osNames.stream()
              .anyMatch(osName -> OS_NAME.toLowerCase(Locale.ROOT).contains(osName)))
          .findFirst()
          .orElse(null);
    }

    public static boolean isWindows() {
      return Objects.equals(WINDOWS, getPlatform());
    }

    public static boolean isMacOs() {
      return Objects.equals(MACOS, getPlatform());
    }

    public static boolean isLinux() {
      return Objects.equals(LINUX, getPlatform());
    }
  }
}
