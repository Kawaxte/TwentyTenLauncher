package io.github.kawaxte.twentyten.launcher;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.languageInstance;

import io.github.kawaxte.twentyten.ELanguage;
import io.github.kawaxte.twentyten.ELookAndFeel;
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

public class Launcher {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(Launcher.class);
  }

  public static void main(String... args) {
    configInstance.load();

    val selectedLanguage = configInstance.getSelectedLanguage();
    languageInstance.load(
        "messages",
        Objects.nonNull(selectedLanguage) && !selectedLanguage.isEmpty()
            ? selectedLanguage
            : ELanguage.USER_LANGUAGE);

    try {
      ELookAndFeel.setLookAndFeel();
    } catch (ClassNotFoundException cnfe) {
      LOGGER.error(
          "Cannot find '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          cnfe);
    } catch (InstantiationException ie) {
      LOGGER.error(
          "Cannot instantiate '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          ie);
    } catch (IllegalAccessException iae) {
      LOGGER.error(
          "Cannot access '{}' on '{}'",
          UIManager.getLookAndFeel().getName(),
          EPlatform.OS_NAME,
          iae);
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

    YggdrasilAuthUtils.validateAndRefreshAccessToken();
  }
}
