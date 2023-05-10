package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JLabel;

/**
 * This class extends the {@link JLabel} class to create a HTML hyperlink.
 *
 * @author Kawaxte
 */
public class JHyperlink extends JLabel {

  /**
   * Creates a new {@link JHyperlink} with the given text.
   *
   * <p>Additionally, the text will be underlined and the cursor will be changed to a hand cursor.
   *
   * @param text the text to display on the hyperlink
   * @param horizontalAlignment the horizontal alignment of the text
   * @see JLabel#JLabel(String, int)
   * @see Cursor#getPredefinedCursor(int)
   */
  public JHyperlink(String text, int horizontalAlignment) {
    super(String.format("<html><u>%s</u></html>", text), horizontalAlignment);

    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  /**
   * Overrides the {@code setText} method to ensure that the text is underlined.
   *
   * @param text the text to display on the hyperlink
   * @see JLabel#setText(String)
   */
  @Override
  public void setText(String text) {
    super.setText(String.format("<html><u>%s</u></html>", text));
  }

  /**
   * Overrides the {@code setForeground} method to ensure that the foreground colour is blue.
   *
   * @param fg the desired foreground colour
   * @see JLabel#setForeground(Color)
   * @see Color
   */
  @Override
  public void setForeground(Color fg) {
    super.setForeground(Color.BLUE);
  }
}
