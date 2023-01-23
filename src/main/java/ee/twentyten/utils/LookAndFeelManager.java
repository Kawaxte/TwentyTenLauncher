package ee.twentyten.utils;

import ee.twentyten.core.ELookAndFeel;
import ee.twentyten.debug.DebugSystem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LookAndFeelManager {

  private LookAndFeelManager() {
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      DebugSystem.println(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setLookAndFeel(ELookAndFeel lookAndFeel) {
    try {
      UIManager.setLookAndFeel(lookAndFeel.getClassName());

      DebugSystem.println(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
