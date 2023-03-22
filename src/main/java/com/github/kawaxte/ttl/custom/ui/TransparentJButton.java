package com.github.kawaxte.ttl.custom.ui;

import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.UIManager;

public class TransparentJButton extends JButton {

  private static final long serialVersionUID = 1L;

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return UIManager.getLookAndFeel().getID().equals("Windows")
        && !(Boolean) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
  }
}
