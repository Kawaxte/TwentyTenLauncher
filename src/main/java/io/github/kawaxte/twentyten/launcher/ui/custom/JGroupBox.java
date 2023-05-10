package io.github.kawaxte.twentyten.launcher.ui.custom;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This class extends the {@code JPanel} class to create a group box, which is simply a panel with a
 * title and a border surrounding it.
 *
 * @author Kawaxte
 */
public class JGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link JGroupBox} with a title, FlowLayout and the specified buffering strategy.
   *
   * <p>It also calls the {@code setTitledBorder(String)} method to set the titled border of the
   * group box.
   *
   * @param title the title of the group box
   * @param isDoubleBuffered a boolean, true for double-buffering, which uses additional memory
   */
  public JGroupBox(String title, boolean isDoubleBuffered) {
    super(isDoubleBuffered);

    this.setTitledBorder(title);
  }

  /**
   * Sets the titled border of the group box by creating an empty border outside and a titled border
   * inside of the compound border.
   *
   * @param title the title of the group box
   * @return the title of the group box
   * @see JPanel#setBorder( Border )
   * @see BorderFactory#createCompoundBorder(Border, Border)
   * @see BorderFactory#createEmptyBorder()
   * @see BorderFactory#createTitledBorder(String)
   */
  public String setTitledBorder(String title) {
    super.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(), BorderFactory.createTitledBorder(title)));
    return title;
  }
}
