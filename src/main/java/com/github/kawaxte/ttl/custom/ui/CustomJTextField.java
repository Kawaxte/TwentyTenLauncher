package com.github.kawaxte.ttl.custom.ui;

import javax.swing.JTextField;
import javax.swing.UIManager;

public class CustomJTextField extends JTextField {

  private static final long serialVersionUID = 1L;

  public CustomJTextField(int columns) {
    super(columns);

    switch (UIManager.getLookAndFeel().getClass().getCanonicalName()) {
      case "com.sun.java.swing.plaf.metal.MetalLookAndFeel":
        this.setColumns(columns - 8);
        break;
      case "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel":
        this.setColumns(columns - 5);
        break;
      case "com.sun.java.swing.plaf.motif.MotifLookAndFeel":
        this.setColumns(columns - 4);
        break;
      default:
        break;
    }
  }
}
