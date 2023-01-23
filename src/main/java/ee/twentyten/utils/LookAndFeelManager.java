package ee.twentyten.utils;

import ee.twentyten.core.ELookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LookAndFeelManager {

  private LookAndFeelManager() {
    throw new UnsupportedOperationException("Utility class is not instantiable");
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setLookAndFeel(ELookAndFeel lookAndFeel) {
    try {
      UIManager.setLookAndFeel(lookAndFeel.getClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
