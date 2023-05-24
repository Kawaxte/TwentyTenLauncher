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

package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper;
import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.util.Objects;
import javax.swing.SwingUtilities;

public class Launcher {

  public static void main(String... args) {
    ELookAndFeel.setLookAndFeel();

    LauncherConfig.loadConfig();
    LauncherLanguage.loadLanguage(
        "messages",
        Objects.nonNull(LauncherConfig.get(0))
            ? (String) LauncherConfig.get(0)
            : ELanguage.USER_LANGUAGE);

    SwingUtilities.invokeLater(() -> new LauncherFrame().setVisible(true));

    boolean microsoftAccessTokenExpired = MicrosoftAuthUtils.isAccessTokenExpired();
    boolean mojangAccessTokenExpired = YggdrasilAuthUtils.isAccessTokenExpired();
    if (mojangAccessTokenExpired) {
      YggdrasilAuthUtils.refreshAccessToken();
    }
    if (microsoftAccessTokenExpired) {
      MicrosoftAuthUtils.refreshAccessToken();
    }
  }

  public static void launchMinecraft(String username, String accessToken, String id) {
    if (Objects.isNull(username)) {
      username = String.format("Player%s", System.currentTimeMillis() % 1000L);
    }

    String sessionId =
        new StringBuilder().append("token:").append(accessToken).append(":").append(id).toString();

    GameAppletWrapper gaw = new GameAppletWrapper(username, sessionId);
    gaw.init();

    LauncherFrame.getInstance().remove(LauncherPanel.getInstance());
    LauncherFrame.getInstance().setContentPane(gaw);
    LauncherFrame.getInstance().revalidate();
    LauncherFrame.getInstance().repaint();

    gaw.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }
}
