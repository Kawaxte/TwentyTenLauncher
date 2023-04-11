package io.github.kawaxte.twentyten.misc.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.text.MessageFormat;
import javax.swing.JLabel;

public class JHyperlink extends JLabel {

  public JHyperlink(String text) {
    super(MessageFormat.format("<html><u>{0}</u></html>",
        text));
  }

  public JHyperlink(String text, int horizontalAlignment) {
    super(MessageFormat.format("<html><u>{0}</u></html>",
        text,
        horizontalAlignment));
  }

  @Override
  public void setText(String text) {
    super.setText(MessageFormat.format("<html><u>{0}</u></html>",
        text));
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(Color.BLUE);
  }

  @Override
  public void setCursor(Cursor cursor) {
    super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }
}
