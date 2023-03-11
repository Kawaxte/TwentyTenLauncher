package ee.twentyten.custom.ui.component;

import ee.twentyten.util.launcher.ui.LookAndFeelUtils;
import javax.swing.JButton;

public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return LookAndFeelUtils.isUsingWindowsClassicTheme;
  }
}
