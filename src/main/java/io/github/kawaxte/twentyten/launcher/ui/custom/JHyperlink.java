package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.Color;
import java.awt.Cursor;
import java.text.MessageFormat;
import javax.swing.JLabel;

public class JHyperlink extends JLabel {

  public JHyperlink(String text, int horizontalAlignment) {
    super(MessageFormat.format("<html><u>{0}</u></html>", text, horizontalAlignment));

    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void setText(String text) {
    super.setText(MessageFormat.format("<html><u>{0}</u></html>", text));
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(Color.BLUE);
  }
}
