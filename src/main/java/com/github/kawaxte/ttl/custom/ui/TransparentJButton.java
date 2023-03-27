package com.github.kawaxte.ttl.custom.ui;

import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.UIManager;

public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return UIManager.getLookAndFeel().getID().equals("Windows")
        && (!(Boolean) toolkit.getDesktopProperty("win.xpstyle.themeActive")
        || UIManager.getLookAndFeel().getName().equals("Windows Classic"));
  }
}
