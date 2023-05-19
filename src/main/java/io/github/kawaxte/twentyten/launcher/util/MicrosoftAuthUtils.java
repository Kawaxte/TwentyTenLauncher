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
import lombok.val;
import org.json.JSONObject;

public final class MicrosoftAuthUtils {

  public static String clientId;

  static {
    clientId = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
  }

  private MicrosoftAuthUtils() {}

  public static void executeMicrosoftAuthWorker(String clientId) {
    if (Objects.isNull(clientId)) {
      throw new NullPointerException("clientId cannot be null");
    }
    if (clientId.isEmpty()) {
      throw new IllegalArgumentException("clientId cannot be empty");
    }

    val consumersDeviceCode = MicrosoftAuth.acquireDeviceCode(clientId);
    if (Objects.isNull(consumersDeviceCode)) {
      return;
    }
    val deviceCodeResponse = getDeviceCodeResponse(consumersDeviceCode);

    LauncherUtils.swapContainers(
        LauncherPanel.instance,
        new MicrosoftAuthPanel(
            deviceCodeResponse[0], deviceCodeResponse[2], deviceCodeResponse[3]));

    new MicrosoftAuthWorker(
            clientId, deviceCodeResponse[1], deviceCodeResponse[3], deviceCodeResponse[4])
        .execute();
  }

  public static boolean isAccessTokenExpired() {
    val refreshToken = (String) LauncherConfig.lookup.get("microsoftRefreshToken");
    if (Objects.isNull(refreshToken)) {
      return false;
    }
    if (refreshToken.isEmpty()) {
      return false;
    }

    val currentTimeSecs = System.currentTimeMillis() / 1000L;
    val accessTokenExpiresIn = LauncherConfig.lookup.get("microsoftAccessTokenExpiresIn");
    val accessTokenExpiresInSecs = Long.parseLong((String) accessTokenExpiresIn) / 1000L;
    val expiresIn = accessTokenExpiresInSecs - currentTimeSecs;
    return expiresIn <= 900;
  }

  public static void refreshAccessToken() {
    val refreshToken = (String) LauncherConfig.lookup.get("microsoftRefreshToken");

    val consumersToken = MicrosoftAuth.refreshToken(clientId, refreshToken);
    if (Objects.isNull(consumersToken)) {
      return;
    }
    val tokenResponse = getRefreshTokenResponse(consumersToken);

    val userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
    if (Objects.isNull(userAuthenticate)) {
      return;
    }
    val xblTokenResponse = MicrosoftAuthTask.getXBLTokenResponse(userAuthenticate);

    val xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
    if (Objects.isNull(xstsAuthorize)) {
      return;
    }
    val xstsTokenResponse = MicrosoftAuthTask.getXSTSTokenResponse(xstsAuthorize);

    val authenticateLoginWithXbox =
        MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    if (Objects.isNull(authenticateLoginWithXbox)) {
      return;
    }
    val accessTokenResponse = MicrosoftAuthTask.getAccessTokenResponse(authenticateLoginWithXbox);
    LauncherConfig.lookup.put("microsoftAccessToken", accessTokenResponse[0]);
    LauncherConfig.lookup.put("microsoftAccessTokenExpiresIn", accessTokenResponse[1]);
    LauncherConfig.lookup.put("microsoftRefreshToken", tokenResponse[1]);
    LauncherConfig.saveConfig();
  }

  private static String[] getRefreshTokenResponse(JSONObject object) {
    val accessToken = object.getString("access_token");
    val refreshToken = object.getString("refresh_token");
    return new String[] {accessToken, refreshToken};
  }

  private static String[] getDeviceCodeResponse(JSONObject object) {
    val userCode = object.getString("user_code");
    val deviceCode = object.getString("device_code");
    val verificationUri = object.getString("verification_uri");
    val expiresIn = String.valueOf(object.getInt("expires_in"));
    val interval = String.valueOf(object.getInt("interval"));
    return new String[] {userCode, deviceCode, verificationUri, expiresIn, interval};
  }
}
