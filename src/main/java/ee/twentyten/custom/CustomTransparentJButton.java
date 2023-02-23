package ee.twentyten.custom;

import ee.twentyten.util.LookAndFeelUtils;
import javax.swing.JButton;

public class CustomTransparentJButton extends JButton {

  public CustomTransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return LookAndFeelUtils.isUsingWindowsClassicTheme;
  }
}
