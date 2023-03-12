package ee.twentyten.custom.ui;

import java.awt.Cursor;
import java.text.MessageFormat;
import javax.swing.JLabel;

public class JHyperlink extends JLabel {

  private static final long serialVersionUID = 1L;

  public JHyperlink(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void setText(String text) {
    super.setText(MessageFormat.format("<html><a href=\"\">{0}</a><html>", text));
  }
}
