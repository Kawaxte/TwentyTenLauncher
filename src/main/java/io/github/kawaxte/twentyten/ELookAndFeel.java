package io.github.kawaxte.twentyten;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;

public enum ELookAndFeel {
  GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
  AQUA("com.apple.laf.AquaLookAndFeel"),
  WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

  @Getter private final String className;

  ELookAndFeel(String className) {
    this.className = className;
  }

  public static void setLookAndFeel()
      throws UnsupportedLookAndFeelException,
          ClassNotFoundException,
          InstantiationException,
          IllegalAccessException {
    if (EPlatform.isLinux()) {
      UIManager.setLookAndFeel(GTK.getClassName());
    }
    if (EPlatform.isMacOS()) {
      UIManager.setLookAndFeel(AQUA.getClassName());
    }
    if (EPlatform.isWindows()) {
      UIManager.setLookAndFeel(WINDOWS.getClassName());
    }
  }
}
