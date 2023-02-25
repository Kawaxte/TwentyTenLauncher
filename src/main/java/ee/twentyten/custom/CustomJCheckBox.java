package ee.twentyten.custom;

import javax.swing.JCheckBox;

public class CustomJCheckBox extends JCheckBox {

  public CustomJCheckBox(String text) {
    super(text);
  }

  @Override
  public boolean isContentAreaFilled() {
    return false;
  }
}
