package io.github.kawaxte.twentyten.launcher.ui.custom;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 * This class extends the {@link JCheckBox} class to make it transparent.
 *
 * @author Kawaxte
 */
public class TransparentJCheckBox extends JCheckBox {

  /**
   * Creates a new {@link TransparentJCheckBox} with the given text.
   *
   * @param text the text to display on the checkbox
   */
  public TransparentJCheckBox(String text) {
    super(text);
  }

  /**
   * Overrides the {@code isOpaque} method to return false, making the checkbox transparent.
   *
   * @return false
   * @see JComponent#isOpaque()
   */
  @Override
  public boolean isOpaque() {
    return false;
  }
}
