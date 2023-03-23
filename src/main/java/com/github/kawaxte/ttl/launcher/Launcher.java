package com.github.kawaxte.ttl.launcher;

import com.github.kawaxte.ttl.launcher.ui.LauncherFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class Launcher {

  private Launcher() {
  }

  public static void main(String... args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException cnfe) {
      cnfe.printStackTrace();
    } catch (InstantiationException ie) {
      ie.printStackTrace();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    } catch (UnsupportedLookAndFeelException ulafe) {
      ulafe.printStackTrace();
    }

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
