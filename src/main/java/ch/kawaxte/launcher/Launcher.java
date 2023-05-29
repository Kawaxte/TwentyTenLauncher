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

package ch.kawaxte.launcher;

import ch.kawaxte.launcher.ui.LauncherFrame;
import ch.kawaxte.launcher.ui.LauncherPanel;
import ch.kawaxte.launcher.ui.MinecraftAppletWrapper;
import ch.kawaxte.launcher.util.MicrosoftAuthUtils;
import ch.kawaxte.launcher.util.YggdrasilAuthUtils;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import javax.swing.SwingUtilities;

/**
 * Class esponsible for setting up the look and feel of the application, loading configuration and
 * language preferences, displaying the main frame of the application, and handling the
 * authentication tokens for both Microsoft and Mojang accounts.
 *
 * <p>It also provides a static method {@link #launchMinecraft(String, String, String)} for
 * launching the Minecraft applet within a JFrame with a specific username, access token and UUID.
 *
 * @author Kawaxte
 * @since 1.3.2823_02
 */
public class Launcher {

  /**
   * Performs the following operations:
   *
   * <ul>
   *   <li>Sets the look and feel of the application.
   *   <li>Loads the configuration and language preferences.
   *   <li>Displays the main application frame.
   *   <li>Refreshes the authentication tokens for Microsoft and Mojang accounts if they are
   *       expired.
   * </ul>
   *
   * @param args command-line arguments
   */
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
   * Constructs a session ID from the provided parameters (excluding {@code username}), and
   * initialises a new {@link MinecraftAppletWrapper} with the constructed session ID. It then
   * removes the current content pane of the {@link LauncherFrame} and sets the initialised {@link
   * MinecraftAppletWrapper} as the new content pane. Finally, it starts the minecraft applet and
   * changes the title of the {@link LauncherFrame} to "Minecraft".
   *
   * <p>If the username is {@code null} or empty, a randomly generated one in the format of
   * "Player###" will be set.
   *
   * @param username username of the account (or email address if Mojang account)
   * @param accessToken the JWT containing various information about the Minecraft profile
   * @param uuid the UUID of the account's Minecraft profile
   * @see MinecraftAppletWrapper#MinecraftAppletWrapper(String, String)
   */
  public static void launchMinecraft(String username, String accessToken, String uuid) {
    if (Objects.isNull(username) || username.isEmpty()) {
      username = String.format("Player%s", System.currentTimeMillis() % 1000L);
    }
    if (Objects.isNull(uuid) || uuid.isEmpty()) {
      uuid =
          UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8))
              .toString()
              .replace("-", "");
    }

    String sessionId =
        new StringBuilder()
            .append("token:")
            .append(accessToken)
            .append(":")
            .append(uuid)
            .toString();

    MinecraftAppletWrapper maw = new MinecraftAppletWrapper(username, sessionId);
    maw.init();

    LauncherFrame.getInstance().remove(LauncherPanel.getInstance());
    LauncherFrame.getInstance().setContentPane(maw);
    LauncherFrame.getInstance().revalidate();
    LauncherFrame.getInstance().repaint();

    maw.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }
}
