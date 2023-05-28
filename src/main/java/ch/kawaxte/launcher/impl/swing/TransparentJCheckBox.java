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

package ch.kawaxte.launcher.impl.swing;

import javax.swing.JCheckBox;

/**
 * Class extending {@link javax.swing.JCheckBox} and overrides the {@code isOpaque()} method to
 * provide transparency for the component.
 *
 * @see javax.swing.JCheckBox
 * @author Kawaxte
 * @since 1.3.2123_01
 */
public class TransparentJCheckBox extends JCheckBox {

  public TransparentJCheckBox(String text) {
    super(text);
  }

  /**
   * This method has been overridden to provide transparency for the {@link javax.swing.JCheckBox}.
   * This is achieved by returning {@code false} for the opaque setting.
   *
   * @return {@code false} to indicate that the {@link javax.swing.JCheckBox} should be transparent
   */
  @Override
  public boolean isOpaque() {
    return false;
  }
}
