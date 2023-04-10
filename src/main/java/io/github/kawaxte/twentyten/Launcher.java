package io.github.kawaxte.twentyten;

import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.ui.LauncherFrame;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.val;
import org.apache.logging.log4j.LogManager;


public class Launcher {

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
}
