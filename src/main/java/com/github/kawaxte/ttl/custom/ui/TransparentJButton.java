package com.github.kawaxte.ttl.custom.ui;

import javax.swing.JButton;

public class TransparentJButton extends JButton {

  private static final long serialVersionUID = 1L;

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return false;
  }
}
