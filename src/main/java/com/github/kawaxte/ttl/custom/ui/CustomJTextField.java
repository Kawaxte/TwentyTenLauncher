package com.github.kawaxte.ttl.custom.ui;

import javax.swing.JTextField;
import javax.swing.UIManager;

public class CustomJTextField extends JTextField {

  public CustomJTextField(int columns) {
    super(columns);

    switch (UIManager.getLookAndFeel().getClass().getCanonicalName()) {
      case "com.apple.laf.AquaLookAndFeel":
      case "javax.swing.plaf.metal.MetalLookAndFeel":
        this.setColumns(columns - 8);
        break;
      case "com.sun.java.swing.plaf.gtk.GTKLookAndFeel":
        this.setColumns(columns - 7);
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
