package com.github.kawaxte.ttl.custom.ui;

import javax.swing.JCheckBox;

public class TransparentJCheckBox extends JCheckBox {

    private static final long serialVersionUID = 1L;

    public TransparentJCheckBox(String text) {
      super(text);
    }

    @Override
    public boolean isOpaque() {
      return false;
    }
}
