package ee.twentyten.core;

import ee.twentyten.debug.DebugSystem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;

@Getter
public enum ELookAndFeel {
  METAL("javax.swing.plaf.metal.MetalLookAndFeel"),
  NIMBUS("javax.swing.plaf.nimbus.NimbusLookAndFeel"),
  MOTIF("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
  AQUA("com.apple.laf.AquaLookAndFeel"),
  GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
  WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
  WINDOWS_CLASSIC("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

  private final String className;

  ELookAndFeel(String className) {
    this.className = className;
  }

  public static ELookAndFeel getCurrentLookAndFeel() {
    String currentLookAndFeelName = UIManager.getLookAndFeel().getClass().getName();
    for (ELookAndFeel lookAndFeel : values()) {
      if (lookAndFeel.className.equals(currentLookAndFeelName)) {
        return lookAndFeel;
      }
    }
    return null;
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      
      DebugSystem.println(UIManager.getLookAndFeel().getClass().getName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setLookAndFeel(ELookAndFeel lookAndFeel) {
    try {
      UIManager.setLookAndFeel(lookAndFeel.getClassName());

      DebugSystem.println(UIManager.getLookAndFeel().getClass().getName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
