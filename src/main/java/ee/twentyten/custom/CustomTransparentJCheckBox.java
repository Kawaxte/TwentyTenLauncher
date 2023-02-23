package ee.twentyten.custom;

import javax.swing.JCheckBox;

public class CustomTransparentJCheckBox extends JCheckBox {

  public CustomTransparentJCheckBox(String text) {
    super(text);
  }

  @Override
  public boolean isContentAreaFilled() {
    return false;
  }
}
