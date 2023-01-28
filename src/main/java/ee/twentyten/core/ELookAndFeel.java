package ee.twentyten.core;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import lombok.Getter;

@Getter
public enum ELookAndFeel {
  AQUA("com.apple.laf.AquaLookAndFeel"),
  GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
  WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

  private final String className;

  ELookAndFeel(String className) {
    this.className = className;
  }

  public static ELookAndFeel getLookAndFeel() {
    String lookAndFeelName = UIManager.getLookAndFeel().getClass().getName();
    for (ELookAndFeel lookAndFeel : values()) {
      if (lookAndFeel.getClassName().equals(lookAndFeelName)) {
        return lookAndFeel;
      }
    }
    return null;
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException e) {
      throw new RuntimeException("Failed to set look and feel", e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to find look and feel class", e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Failed to instantiate look and feel class", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to access look and feel class", e);
    }
  }
}
