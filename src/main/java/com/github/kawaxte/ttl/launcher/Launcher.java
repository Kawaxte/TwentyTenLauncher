package com.github.kawaxte.ttl.launcher;

import com.github.kawaxte.ttl.launcher.ui.LauncherFrame;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Launcher {
  private Launcher() {
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(WindowsClassicLookAndFeel.class.getName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(LauncherFrame::new);
  }
}
