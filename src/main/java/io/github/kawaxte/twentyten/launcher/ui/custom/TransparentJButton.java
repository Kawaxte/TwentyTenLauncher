/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.kawaxte.twentyten.launcher.ui.custom;

import java.awt.Toolkit;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 * This class extends {@link javax.swing.JButton} and overrides the {@link
 * javax.swing.JComponent#isOpaque()} method to provide a tailored behaviour for button transparency
 * in certain Windows environments. Specifically, it provides support for Windows NT kernels between
 * version 5.1 and 6.1 when set to "Windows Classic" theme.
 *
 * <p>In these specific environments, a standard opaque setting can cause the button's core drawn
 * elements to completely disappear. The overridden implementation in this class addresses this
 * issue, ensuring the button is displayed as expected.
 *
 * @see javax.swing.JButton
 * @author Kawaxte
 * @since 1.4.0823_01
 */
public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  /**
   * This method has been overridden to provide custom behaviour in certain Windows environments
   * where the standard opaque setting causes the button's core drawn elements to disappear. This
   * particularly affects users who have their theme set to "Windows Classic" on Windows NT kernels
   * between version 5.1 and 6.1.
   *
   * @return {@code true} if the {@link javax.swing.JButton} should be opaque, {@code false}
   *     otherwise.
   */
  @Override
  public boolean isOpaque() {
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    String id = UIManager.getLookAndFeel().getID();

    boolean windows = Objects.equals(id, "Windows");
    Boolean winXpStyleThemeActive =
        (Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive");
    boolean windowsClassic = UIManager.getLookAndFeel().getName().equals("Windows Classic");
    return windows && (!winXpStyleThemeActive || windowsClassic);
  }
}
