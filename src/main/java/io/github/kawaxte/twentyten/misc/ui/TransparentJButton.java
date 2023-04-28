package io.github.kawaxte.twentyten.misc.ui;

import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.UIManager;
import lombok.val;

public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    val defaultToolkit = Toolkit.getDefaultToolkit();
    return UIManager.getLookAndFeel().getID().equals("Windows")
        && (!(Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive")
            || UIManager.getLookAndFeel().getName().equals("Windows Classic"));
  }
}
