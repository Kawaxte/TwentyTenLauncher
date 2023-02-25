package ee.twentyten.custom;

import ee.twentyten.util.LookAndFeelUtils;
import javax.swing.JButton;

public class CustomJButton extends JButton {

  public CustomJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    return LookAndFeelUtils.isUsingWindowsClassicTheme;
  }
}
