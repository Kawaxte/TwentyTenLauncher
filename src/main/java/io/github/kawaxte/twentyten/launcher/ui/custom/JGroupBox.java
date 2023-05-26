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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * This class extends {@link javax.swing.JPanel} and provides a titled border to mimic the GroupBox
 * functionality found in the System.Windows.Forms library in C#. It allows for the setting of a
 * title when the group box is created and also provides a method to change this title.
 *
 * <p>The border is constructed with an empty margin on the outside and a titled border on the
 * inside.
 *
 * @see javax.swing.JPanel
 */
public class JGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;

  public JGroupBox(String title, boolean isDoubleBuffered) {
    super(isDoubleBuffered);

    this.setTitledBorder(title);
  }

  /**
   * Sets a titled border to this {@link javax.swing.JPanel}.
   *
   * <p>A compound border is created with an empty border on the outside and a titled border on the
   * inside. The title of the border is set to the passed in {@code title} parameter.
   *
   * @param title The title to set on the border.
   * @return The set title.
   * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
   * @see javax.swing.BorderFactory#createCompoundBorder(javax.swing.border.Border,
   *     javax.swing.border.Border)
   */
  public String setTitledBorder(String title) {
    super.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(), BorderFactory.createTitledBorder(title)));
    return title;
  }
}
