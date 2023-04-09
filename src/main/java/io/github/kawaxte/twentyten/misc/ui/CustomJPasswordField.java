package io.github.kawaxte.twentyten.misc.ui;

import javax.swing.JPasswordField;
import javax.swing.UIManager;

public class CustomJPasswordField extends JPasswordField {

  public CustomJPasswordField(int columns) {
    super(columns);

    switch (UIManager.getLookAndFeel().getClass().getCanonicalName()) {
      case "com.apple.laf.AquaLookAndFeel":
      case "javax.swing.plaf.metal.MetalLookAndFeel":
        this.setColumns(columns - 8);
        break;
      case "com.sun.java.swing.plaf.gtk.GTKLookAndFeel":
        this.setColumns(columns - 7);
        break;
      default:
        break;
    }
  }
}
