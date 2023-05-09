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
    Objects.requireNonNull(consumersDeviceCode, "consumersDeviceCode cannot be null");
    val deviceCodeResponse = getDeviceCodeResponse(consumersDeviceCode);

    LauncherUtils.addComponentToContainer(
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
    Objects.requireNonNull(consumersToken, "consumersToken cannot be null");
    val tokenResponse = getRefreshTokenResponse(consumersToken);

    val userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
    Objects.requireNonNull(userAuthenticate, "userAuthenticate cannot be null");
    val xblTokenResponse = MicrosoftAuthTask.getXBLTokenResponse(userAuthenticate);

    val xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
    Objects.requireNonNull(xstsAuthorize, "xstsAuthorize cannot be null");
    val xstsTokenResponse = MicrosoftAuthTask.getXSTSTokenResponse(xstsAuthorize);

    val authenticateLoginWithXbox =
        MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    Objects.requireNonNull(authenticateLoginWithXbox, "authenticateLoginWithXbox cannot be null");
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
