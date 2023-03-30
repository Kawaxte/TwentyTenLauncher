package com.github.kawaxte.twentyten;

import com.github.kawaxte.twentyten.lang.LauncherLanguage;
import com.github.kawaxte.twentyten.log.LauncherLogger;
import com.github.kawaxte.twentyten.ui.LauncherFrame;
import com.github.kawaxte.twentyten.util.LauncherLoggerUtils.ELevel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Launcher {

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException cnfe) {
      LauncherLogger.log(cnfe, "{0} could not found", cnfe.getMessage());
    } catch (InstantiationException ie) {
      LauncherLogger.log(ie, "{0} is not instantiable", ie.getMessage());
    } catch (IllegalAccessException iae) {
      LauncherLogger.log(iae, "{0} is not accessible", iae.getMessage());
    } catch (UnsupportedLookAndFeelException ulafe) {
      LauncherLogger.log(ulafe, "{0} is not supported", ulafe.getMessage());
    } finally {
      LauncherLogger.log(ELevel.INFO, "Using Look and Feel: {0}",
          UIManager.getLookAndFeel().getClass().getCanonicalName());
    }

    LauncherLanguage.load("messages", "en");

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
