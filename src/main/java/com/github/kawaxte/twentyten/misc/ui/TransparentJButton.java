package com.github.kawaxte.twentyten.misc.ui;

import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.UIManager;
import lombok.var;

public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    var defaultToolkit = Toolkit.getDefaultToolkit();
    return UIManager.getLookAndFeel().getID().equals("Windows")
        && (!(Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive")
        || UIManager.getLookAndFeel().getName().equals("Windows Classic"));
  }
}
