package io.github.kawaxte.twentyten;

import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.ui.LauncherFrame;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import io.github.kawaxte.twentyten.util.LauncherUtils.EPlatform;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.logging.log4j.LogManager;


public class Launcher {

  static {
    LauncherUtils.logger = LogManager.getLogger(Launcher.class);
  }

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException cnfe) {
      LauncherUtils.logger.error("{} not found",
          cnfe.getMessage(),
          cnfe);
    } catch (InstantiationException ie) {
      LauncherUtils.logger.error("{} is not instantiable",
          ie.getMessage(),
          ie);
    } catch (IllegalAccessException iae) {
      LauncherUtils.logger.error("{} is not accessible",
          iae.getMessage(),
          iae);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LauncherUtils.logger.error("{} is not supported on {}",
          ulafe.getMessage(),
          EPlatform.getPlatform(),
          ulafe);
    } finally {
      LauncherUtils.logger.info("Using {} look and feel",
          UIManager.getLookAndFeel().getName());
    }

    LauncherLanguage.loadLanguage("messages",
        System.getProperty("user.language"));

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
