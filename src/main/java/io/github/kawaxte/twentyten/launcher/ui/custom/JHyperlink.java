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

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JLabel;

public class JHyperlink extends JLabel {

  public JHyperlink(String text, int horizontalAlignment) {
    super(String.format("<html><u>%s</u></html>", text), horizontalAlignment);

    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  @Override
  public void setText(String text) {
    super.setText(String.format("<html><u>%s</u></html>", text));
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(Color.BLUE);
  }
}
