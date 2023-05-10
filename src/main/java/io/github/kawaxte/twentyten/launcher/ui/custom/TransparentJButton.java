package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.Toolkit;
import java.util.Objects;
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
    val windowsId = UIManager.getLookAndFeel().getID();
    val windows = Objects.equals(windowsId, "Windows");
    val winXpStyleThemeActive =
        (Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive");
    val windowsClassic = UIManager.getLookAndFeel().getName().equals("Windows Classic");
    return windows && (!winXpStyleThemeActive || windowsClassic);
  }
}
