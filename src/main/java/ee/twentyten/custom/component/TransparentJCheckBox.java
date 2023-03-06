package ee.twentyten.custom.component;

import javax.swing.JCheckBox;

public class TransparentJCheckBox extends JCheckBox {

  public TransparentJCheckBox(String text) {
    super(text);
  }

  @Override
  public boolean isContentAreaFilled() {
    return false;
  }
}
