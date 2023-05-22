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

public class TransparentJButton extends JButton {

  public TransparentJButton(String text) {
    super(text);
  }

  @Override
  public boolean isOpaque() {
    Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    String windowsId = UIManager.getLookAndFeel().getID();

    boolean windows = Objects.equals(windowsId, "Windows");
    Boolean winXpStyleThemeActive =
        (Boolean) defaultToolkit.getDesktopProperty("win.xpstyle.themeActive");
    boolean windowsClassic = UIManager.getLookAndFeel().getName().equals("Windows Classic");
    return windows && (!winXpStyleThemeActive || windowsClassic);
  }
}
