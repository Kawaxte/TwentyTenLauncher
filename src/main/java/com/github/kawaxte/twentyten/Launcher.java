package com.github.kawaxte.twentyten;

import com.github.kawaxte.twentyten.lang.LauncherLanguage;
import com.github.kawaxte.twentyten.ui.LauncherFrame;
import com.github.kawaxte.twentyten.util.LauncherUtils;
import com.github.kawaxte.twentyten.util.LauncherUtils.EPlatform;
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
      LauncherUtils.logger.error("Class \"{}\" could not be found",
          cnfe.getMessage(),
          cnfe);
    } catch (InstantiationException ie) {
      LauncherUtils.logger.error("Class \"{}\" cannot be instantiated",
          ie.getMessage(),
          ie);
    } catch (IllegalAccessException iae) {
      LauncherUtils.logger.error("Class \"{}\" is not accessible",
          iae.getMessage(),
          iae);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LauncherUtils.logger.error("Class \"{}\" is not supported on {}",
          ulafe.getMessage(),
          EPlatform.getPlatform(),
          ulafe);
    } finally {
      LauncherUtils.logger.info("Look and Feel \"{}\" set",
          UIManager.getLookAndFeel().getName());
    }

    LauncherLanguage.loadLanguage("messages", "en");

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
