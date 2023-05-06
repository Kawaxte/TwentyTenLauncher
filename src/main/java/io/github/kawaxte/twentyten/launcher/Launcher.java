package io.github.kawaxte.twentyten.launcher;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;

import io.github.kawaxte.twentyten.EPlatform;
import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.util.Objects;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Launcher {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(Launcher.class);
  }

  private Launcher() {}

  public static void main(String... args) {
    CONFIG.load();

    val selectedLanguage = CONFIG.getSelectedLanguage();
    LANGUAGE.load(
        "messages",
        Objects.nonNull(selectedLanguage) && !selectedLanguage.isEmpty()
            ? selectedLanguage
            : System.getProperty("user.language"));

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ReflectiveOperationException roe) {
      LOGGER.error("Failed to set look and feel", roe);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LOGGER.error(
          "'{}' is not supported on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          ulafe);
    } finally {
      LOGGER.info("Set look and feel to '{}'", UIManager.getLookAndFeel().getName());
    }

    SwingUtilities.invokeLater(
        () -> {
          val launcherFrame = new LauncherFrame();
          launcherFrame.setVisible(true);
        });

    YggdrasilAuthUtils.refresh(CONFIG.getMojangAccessToken(), CONFIG.getMojangClientToken());
  }
}
