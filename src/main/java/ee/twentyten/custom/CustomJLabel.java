package ee.twentyten.custom;

import java.awt.Cursor;
import javax.swing.JLabel;

public class CustomJLabel extends JLabel {

  private static final long serialVersionUID = 1L;

  public CustomJLabel(String text, int horizontalAlignment) {
    super(String.format("<html><a href=\"\">%s</a><html>", text), horizontalAlignment);
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void setText(String text) {
    super.setText(String.format("<html><a href=\"\">%s</a><html>", text));
  }
}
