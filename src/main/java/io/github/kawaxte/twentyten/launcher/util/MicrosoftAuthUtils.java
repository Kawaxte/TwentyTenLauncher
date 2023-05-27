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

package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth;
import io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthTask;
import io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthWorker;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import java.util.Objects;
import org.json.JSONObject;

/**
 * Utility class for handling Microsoft authentication.
 *
 * @author Kawaxte
 * @since 1.5.0823_02
 */
public final class MicrosoftAuthUtils {

  public static final String AZURE_CLIENT_ID;

  static {
    AZURE_CLIENT_ID = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
  }

  private MicrosoftAuthUtils() {}

  /**
   * Executes the Microsoft authentication worker as long as the given {@code clientId} is not
   * {@code null} or empty.
   *
   * <p>It also swaps the containers ({@link javax.swing.JPanel}s) from {@link
   * io.github.kawaxte.twentyten.launcher.ui.LauncherPanel} to {@link
   * io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel}.
   *
   * @param clientId the client ID of the Azure application required for authentication
   */
  public static void executeMicrosoftAuthWorker(String clientId) {
    if (Objects.isNull(clientId)) {
      throw new NullPointerException("clientId cannot be null");
    }
    if (clientId.isEmpty()) {
      throw new IllegalArgumentException("clientId cannot be empty");
    }

    JSONObject consumersDeviceCode = MicrosoftAuth.acquireDeviceCode(clientId);
    if (Objects.isNull(consumersDeviceCode)) {
      return;
    }
    String[] deviceCodeResponse = MicrosoftAuthTask.getDeviceCodeResponse(consumersDeviceCode);

    LauncherUtils.swapContainers(
        LauncherPanel.getInstance(),
        new MicrosoftAuthPanel(
            deviceCodeResponse[0], deviceCodeResponse[2], deviceCodeResponse[3]));

    new MicrosoftAuthWorker(
            clientId, deviceCodeResponse[1], deviceCodeResponse[3], deviceCodeResponse[4])
        .execute();
  }

  /**
   * Checks if the access token is expired as long as the refresh token is not {@code null} or
   * empty.
   *
   * <p>It checks the current time in seconds and compares it to the expiration time of the access
   * token.
   *
   * @return {@code true} if the difference between the current time and the expiration time is less
   *     than or equal to 900 seconds (15 minutes), {@code false} otherwise
   */
  public static boolean isAccessTokenExpired() {
    String refreshToken = (String) LauncherConfig.get(9);
    if (Objects.isNull(refreshToken)) {
      return false;
    }
    if (refreshToken.isEmpty()) {
      return false;
    }

    long currentTimeSecs = System.currentTimeMillis() / 1000L;
    String accessTokenExpiresIn = (String) LauncherConfig.get(8);
    long accessTokenExpiresInSecs = Long.parseLong(accessTokenExpiresIn) / 1000L;
    long expiresIn = accessTokenExpiresInSecs - currentTimeSecs;
    return expiresIn <= 900;
  }

  /**
   * Refreshes the access token and saves the new access token, refresh token, and expiration time
   * to the configuration file.
   *
   * @see io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth#refreshToken(String, String)
   * @see io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth#acquireXBLToken(String)
   * @see io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth#acquireXSTSToken(String)
   * @see io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuth#acquireAccessToken(String, String)
   */
  public static void refreshAccessToken() {
    String refreshToken = (String) LauncherConfig.get(9);

    JSONObject consumersToken = MicrosoftAuth.refreshToken(AZURE_CLIENT_ID, refreshToken);
    if (Objects.isNull(consumersToken)) {
      return;
    }
    String[] tokenResponse = MicrosoftAuthTask.getRefreshTokenResponse(consumersToken);

    JSONObject userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
    if (Objects.isNull(userAuthenticate)) {
      return;
    }
    String[] xblTokenResponse = MicrosoftAuthTask.getXBLTokenResponse(userAuthenticate);

    JSONObject xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
    if (Objects.isNull(xstsAuthorize)) {
      return;
    }
    String xstsTokenResponse = MicrosoftAuthTask.getXSTSTokenResponse(xstsAuthorize);

    JSONObject authenticateLoginWithXbox =
        MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    if (Objects.isNull(authenticateLoginWithXbox)) {
      return;
    }

    String[] accessTokenResponse =
        MicrosoftAuthTask.getAccessTokenResponse(authenticateLoginWithXbox);
    LauncherConfig.set(7, accessTokenResponse[0]);
    LauncherConfig.set(8, accessTokenResponse[1]);
    LauncherConfig.set(9, tokenResponse[1]);
    LauncherConfig.saveConfig();
  }
}
