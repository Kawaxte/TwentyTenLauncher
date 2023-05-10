package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.Toolkit;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import lombok.val;

/**
 * This class extends the {@link JButton} class to fix an issue with the Swing API's implementation
 * of the "Windows Classic" look and feel, where transparent buttons are completely invisible if
 * opaque.
 *
 * <p>It overrides the {@code isOpaque} method to disable the opaque property of the {@link JButton}
 * if the condition is met, and enable it otherwise. This ensures that the default {@link
 * JComponent} background is no longer visible if the button is opaque.
 *
 * @author Kawaxte
 */
public class TransparentJButton extends JButton {

  /**
   * Creates a new {@link TransparentJButton} with the given text.
   *
   * @param text the text to display on the button
   */
  public TransparentJButton(String text) {
    super(text);
  }

  /**
   * Overrides the {@code isOpaque} method to determine whether the button should be opaque or not
   * based on the current look and feel.
   *
   * <p>If the look and feel is "Windows Classic" and the property for the the XP-style theme is
   * disabled, the button will not be opaque. Otherwise, the button will be opaque.
   *
   * @return true if the button is opaque, false otherwise
   * @see JComponent#isOpaque()
   */
  @Override
  public boolean isOpaque() {
    val defaultToolkit = Toolkit.getDefaultToolkit();
    val windowsId = UIManager.getLookAndFeel().getID();
    val windows = Objects.equals(windowsId, "Windows");
    val winXpStyleThemeActive =
        (Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive");
    val windowsClassic = UIManager.getLookAndFeel().getName().equals("Windows Classic");
    return windows && (!winXpStyleThemeActive || windowsClassic);
  }
}
