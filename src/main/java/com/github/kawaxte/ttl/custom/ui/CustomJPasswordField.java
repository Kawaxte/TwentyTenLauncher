package com.github.kawaxte.ttl.custom.ui;

import javax.swing.JPasswordField;
import javax.swing.UIManager;

public class CustomJPasswordField extends JPasswordField {

  private static final long serialVersionUID = 1L;

  public CustomJPasswordField(int columns) {
    super(columns);

    switch (UIManager.getLookAndFeel().getClass().getCanonicalName()) {
      case "com.apple.laf.AquaLookAndFeel":
      case "javax.swing.plaf.metal.MetalLookAndFeel":
        this.setColumns(columns - 8);
        break;
      case "com.sun.java.swing.plaf.motif.MotifLookAndFeel":
        this.setColumns(columns - 4);
        break;
      case "javax.swing.plaf.nimbus.NimbusLookAndFeel":
        this.setColumns(columns - 5);
        break;
      default:
        break;
    }
  }
}
