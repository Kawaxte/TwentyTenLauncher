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

/**
 * This is the main class of the launcher. It prepares all the necessary configurations to start the
 * game.
 *
 * <p>The preparation includes the following steps:
 *
 * <ol>
 *   <li>Setting the system look and feel.
 *   <li>Loading the launcher configuration file from the working directory.
 *   <li>Loading the appropriate language resources based on the setting in the configuration file,
 *       or the system language if the configuration file is not found or the setting is invalid.
 *   <li>Showing the main window of the launcher.
 *   <li>Checking and refreshing the access tokens for Legacy, Mojang and Microsoft accounts if they
 *       are expired.
 * </ol>
 *
 * <p>The class also contains a method to launch Minecraft itself, taking a username, an access
 * token, and the UUID of the account as parameters.
 *
 * @author Kawaxte
 * @since 1.3.2823_02
 */
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

  /**
   * This method is used to launch Minecraft. It takes the username, access token, and UUID of the
   * account as parameters. If the username is null, it generates a temporary username based on the
   * current timestamp (e.g Player123).
   *
   * <p>It then creates and initialises a {@link
   * io.github.kawaxte.twentyten.launcher.ui.GameAppletWrapper} with the provided username and
   * sessionId, removes the {@link io.github.kawaxte.twentyten.launcher.ui.LauncherPanel} from the
   * {@link io.github.kawaxte.twentyten.launcher.ui.LauncherFrame}, sets the content pane of the
   * LauncherFrame to the GameAppletWrapper, and starts the game.
   *
   * @param username The username for the Minecraft account. If it is {@code null}, a temporary
   *     username is generated.
   * @param accessToken The access token for the Minecraft account.
   * @param uuid The UUID associated with the Minecraft account.
   */
  public static void launchMinecraft(String username, String accessToken, String uuid) {
    if (Objects.isNull(username)) {
      username = String.format("Player%s", System.currentTimeMillis() % 1000L);
    }

    String sessionId =
        new StringBuilder()
            .append("token:")
            .append(accessToken)
            .append(":")
            .append(uuid)
            .toString();

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
