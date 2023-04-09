package io.github.kawaxte.twentyten.misc.ui;

import javax.swing.JCheckBox;

public class TransparentJCheckBox extends JCheckBox {

  public TransparentJCheckBox(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return false;
  }
}
