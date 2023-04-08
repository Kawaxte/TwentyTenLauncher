package com.github.kawaxte.twentyten.misc.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JLabel;

public class CustomJLabel extends JLabel {

  public static final int HYPERLINK;
  public static final int ERROR;

  static {
    HYPERLINK = 0;
    ERROR = 1;
  }

  public CustomJLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
  }

  public CustomJLabel(String text, int horizontalAlignment, int type) {
    super(text, horizontalAlignment);

    switch (type) {
      case 0:
        this.setText(String.format("<html><u>%s</u></html>", text));
        this.setForeground(Color.BLUE);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        break;
      case 1:
        this.setFont(this.getFont().deriveFont(Font.ITALIC, 16F));
        this.setForeground(Color.RED.darker());
        break;
      default:
        break;
    }
  }
}
