package ee.twentyten.custom.component;

import ee.twentyten.util.LookAndFeelUtils;
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
