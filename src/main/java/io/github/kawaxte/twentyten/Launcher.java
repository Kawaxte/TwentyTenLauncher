package io.github.kawaxte.twentyten;

import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.ui.LauncherFrame;
import io.github.kawaxte.twentyten.util.LauncherUtils;
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


public final class Launcher {

  private Launcher() {
  }

  static {
    LauncherUtils.logger = LogManager.getLogger(Launcher.class);
  }

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ReflectiveOperationException roe) {
      LauncherUtils.logger.error("Failed to set '{}' as look and feel on '{}'",
          roe.getMessage(),
          System.getProperty("os.name"),
          roe);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LauncherUtils.logger.error("'{}' is not supported on '{}'",
          UIManager.getLookAndFeel().getName(),
          System.getProperty("os.name"),
          ulafe);
    } finally {
      LauncherUtils.logger.info("Using '{}' as look and feel on '{}'",
          UIManager.getLookAndFeel().getName(),
          System.getProperty("os.name"));
    }

    LauncherLanguage.loadLanguage("messages",
        System.getProperty("user.language"));

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
      return Arrays.stream(values()).filter(platform -> platform.osNames.stream()
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
