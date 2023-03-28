package com.github.kawaxte.twentyten;

import com.github.kawaxte.twentyten.log.LauncherLogger;
import com.github.kawaxte.twentyten.ui.LauncherFrame;
import com.github.kawaxte.twentyten.util.LoggerUtils.ELevel;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class Launcher {

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      Arrays.stream(UIManager.getInstalledLookAndFeels()).forEachOrdered(
          ilaf -> LauncherLogger.log(ELevel.INFO, "Available LAFs: {0}", ilaf.getClassName()));
      LauncherLogger.log(ELevel.INFO, "Using LAF: {0}", UIManager.getLookAndFeel().getClass());
    } catch (ClassNotFoundException cnfe) {
      LauncherLogger.log(cnfe, "{0} could not found", cnfe.getMessage());
    } catch (InstantiationException ie) {
      LauncherLogger.log(ie, "{0} is not instantiable", ie.getMessage());
    } catch (IllegalAccessException iae) {
      LauncherLogger.log(iae, "{0} is not accessible", iae.getMessage());
    } catch (UnsupportedLookAndFeelException ulafe) {
      LauncherLogger.log(ulafe, "{0} is not supported", ulafe.getMessage());
    }

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
