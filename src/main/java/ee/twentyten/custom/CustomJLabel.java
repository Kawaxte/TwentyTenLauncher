package ee.twentyten.custom;

import java.awt.Cursor;
import java.text.MessageFormat;
import javax.swing.JLabel;

public class CustomJLabel extends JLabel {

  private static final long serialVersionUID = 1L;

  public CustomJLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void setText(String text) {
    super.setText(MessageFormat.format("<html><a href=\"\">{0}</a><html>", text));
  }
}
