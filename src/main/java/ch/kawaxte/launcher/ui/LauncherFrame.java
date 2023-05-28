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

package ch.kawaxte.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Optional;
import javax.swing.JFrame;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private static LauncherFrame instance;

  public LauncherFrame() {
    super();

    setInstance(this);

    URL iconUrl =
        Optional.ofNullable(LauncherFrame.class.getClassLoader().getResource("favicon.png"))
            .orElseThrow(() -> new NullPointerException("iconUrl cannot be null"));
    this.setIconImage(this.getToolkit().getImage(iconUrl));

    this.setLayout(new BorderLayout());

    this.setContentPane(new LauncherPanel());

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(640 + 16, 480 + 39));
    this.setPreferredSize(new Dimension(854 + 16, 480 + 39));

    this.pack();

    this.setLocationRelativeTo(null);
    this.setResizable(true);
  }

  public static LauncherFrame getInstance() {
    return instance;
  }

  private static void setInstance(LauncherFrame lf) {
    instance = lf;
  }

  @Override
  public String getTitle() {
    return "TwentyTen Launcher";
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    this.getContentPane().setFont(font);
  }
}
