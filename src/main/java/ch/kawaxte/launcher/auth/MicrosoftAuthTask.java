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

package ch.kawaxte.launcher.auth;

import ch.kawaxte.launcher.Launcher;
import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.ui.LauncherNoNetworkPanel;
import ch.kawaxte.launcher.ui.LauncherPanel;
import ch.kawaxte.launcher.ui.MicrosoftAuthPanel;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task performs the authentication process when run, sending the necessary credentials and
 * handling the response. The response may indicate an error, a successful login without the
 * Minecraft profile, or a successful login with the Minecraft profile.
 *
 * <p>Upon receiving a response, the task updates the {@link LauncherConfig} with newly obtained
 * properties and initiates the Minecraft launch process with the appropriate parameters based on
 * the response.
 *
 * @see Runnable
 * @author Kawaxte
 * @since 1.5.0823_02
 * @see MicrosoftAuth
 * @see MicrosoftAuthWorker
 */
public class MicrosoftAuthTask implements Runnable {

  private static final Logger LOGGER;

  static {
    LOGGER = LoggerFactory.getLogger(MicrosoftAuthTask.class);
  }

  private final ScheduledExecutorService service;
  private final String clientId;
  private final String deviceCode;

  /**
   * Constructs a new authentication task with the given parameters.
   *
   * @param service The executor service to use
   * @param clientId The client ID of the Azure application to give permissions to
   * @param deviceCode The device code to retrieve the access token with
   */
  public MicrosoftAuthTask(ScheduledExecutorService service, String clientId, String deviceCode) {
    this.service = service;
    this.clientId = clientId;
    this.deviceCode = deviceCode;
  }

  /**
   * Returns the user code, device code, verification URI, expires-in, and interval as an array of
   * strings.
   *
   * <p>Each obtained parameter has its use case as follows:
   *
   * <ul>
   *   <li>{@code userCode} is the code that the user must enter on the verification URI
   *   <li>{@code deviceCode} is the code that the device must send to the Azure application
   *   <li>{@code verificationUri} is the URI that the user must visit to enter the user code
   *   <li>{@code expiresIn} is the number of seconds until the device code expires
   *   <li>{@code interval} is the number of seconds to wait before polling for the access token
   * </ul>
   *
   * @param object the JSON object containing the response from the device code request
   * @return {@code userCode}, {@code deviceCode}, {@code verificationUri}, {@code expiresIn}, and
   *     {@code interval} as an array of strings
   */
  public static String[] getDeviceCodeResponse(JSONObject object) {
    String userCode = object.getString("user_code");
    String deviceCode = object.getString("device_code");
    String verificationUri = object.getString("verification_uri");
    String expiresIn = String.valueOf(object.getInt("expires_in"));
    String interval = String.valueOf(object.getInt("interval"));
    return new String[] {userCode, deviceCode, verificationUri, expiresIn, interval};
  }

  /**
   * Returns the access token and refresh token as an array of strings.
   *
   * <p>This method also handles specific errors that may occur whilst attempting to retrieve the
   * access token and refresh token. It also the only method to adjust the UI to show the progress
   * of the polling request.
   *
   * @param object the JSON object containing the response from the polling request
   * @return {@code accessToken} and {@code refreshToken} as an array of strings
   */
  private static String[] getTokenResponse(JSONObject object) {
    if (object.has("error")) {
      String error = object.getString("error");
      if (Objects.equals(error, "authorization_pending")) {
        JProgressBar progressBar = MicrosoftAuthPanel.getInstance().getExpiresInProgressBar();
        progressBar.setValue(progressBar.getValue() - 1);
        return new String[0];
      }

      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
      return new String[0];
    }

    MicrosoftAuthPanel.getInstance().getCopyCodeLabel().setVisible(false);
    MicrosoftAuthPanel.getInstance().getUserCodeLabel().setVisible(false);
    MicrosoftAuthPanel.getInstance().getExpiresInProgressBar().setIndeterminate(true);
    MicrosoftAuthPanel.getInstance().getOpenBrowserButton().setVisible(false);

    String accessToken = object.getString("access_token");
    String refreshToken = object.getString("refresh_token");
    return new String[] {accessToken, refreshToken};
  }

  /**
   * Returns the access token and refresh token as an array of strings.
   *
   * @param object the JSON object containing the response from the token request
   * @return {@code accessToken} and {@code refreshToken} as an array of strings
   */
  public static String[] getRefreshTokenResponse(JSONObject object) {
    String accessToken = object.getString("access_token");
    String refreshToken = object.getString("refresh_token");
    return new String[] {accessToken, refreshToken};
  }

  /**
   * Returns the user hash and XBL token as an array of strings.
   *
   * @param object the JSON object containing the response from the token request
   * @return {@code uhs} and {@code token} as an array of strings
   */
  public static String[] getXBLTokenResponse(JSONObject object) {
    JSONObject displayClaims = object.getJSONObject("DisplayClaims");
    JSONArray xui = displayClaims.getJSONArray("xui");

    String uhs = xui.getJSONObject(0).getString("uhs");
    String token = object.getString("Token");
    return new String[] {uhs, token};
  }

  /**
   * Returns the XSTS token as a string.
   *
   * <p>This method also handles specific errors that may occur whilst attempting to retrieve the
   * XSTS token.
   *
   * @param object the JSON object containing the response from the XBL token request
   * @return {@code token} as a string
   */
  public static String getXSTSTokenResponse(JSONObject object) {
    if (object.has("XErr")) {
      long xerr = object.getLong("XErr");
      switch ((int) xerr) {
        case (int) 2148916233L:
          LauncherUtils.swapContainers(
              LauncherPanel.getInstance(),
              new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[3]));
          break;
        case (int) 2148916238L:
          LauncherUtils.swapContainers(
              LauncherPanel.getInstance(),
              new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[4]));
          break;
        default:
          LauncherUtils.swapContainers(
              LauncherPanel.getInstance(),
              new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
          break;
      }

      LOGGER.error("{}", xerr);
      return null;
    }
    return object.getString("Token");
  }

  /**
   * Returns the Minecraft access token and its expiry time as an array of strings.
   *
   * <p>The expiry time is converted to milliseconds and added to the current time.
   *
   * @param object the JSON object containing the response from the XSST token request
   * @return {@code accessToken} and {@code expiresInMillis} as an array of strings
   */
  public static String[] getAccessTokenResponse(JSONObject object) {
    String accessToken = object.getString("access_token");
    int expiresIn = object.getInt("expires_in");
    long expiresInMillis = System.currentTimeMillis() + (expiresIn * 1000L);
    return new String[] {accessToken, String.valueOf(expiresInMillis)};
  }

  /**
   * Checks if the given JSON object contains the "game_minecraft" item.
   *
   * <p>The "game_minecraft" item refers to Minecraft (Java Edition).
   *
   * @param object the JSON object to check for the game_minecraft item
   * @return {@code true} if the JSON object contains the game_minecraft item, otherwise {@code
   *     false}
   */
  private static boolean isItemNameEqualToGameMinecraft(JSONObject object) {
    JSONArray items = object.getJSONArray("items");
    if (!items.isEmpty()) {
      for (Object item : items) {
        String name = ((JSONObject) item).getString("name");
        if (Objects.equals(name, "game_minecraft")) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns the Minecraft profile UUID and name as an array of strings.
   *
   * @param object the JSON object containing the response from the Minecraft profile request
   * @return {@code id} and {@code name} as an array of strings
   */
  private static String[] getMinecraftProfileResponse(JSONObject object) {
    String id = object.getString("id");
    String name = object.getString("name");
    return new String[] {id, name};
  }

  /**
   * Performs the Microsoft authentication process using the supplied parameters. Upon receiving a
   * response, it updates the launcher configuration with the new session information.
   *
   * <p>If an error is received or the account does not own Minecraft (Java Edition), it will launch
   * the minecraft with a generic player name. If the account does own Minecraft, it will launch the
   * minecraft with the authenticated user's profile.
   *
   * @see MicrosoftAuth#acquireToken(String, String)
   * @see MicrosoftAuth#acquireXBLToken(String)
   * @see MicrosoftAuth#acquireXSTSToken(String)
   * @see MicrosoftAuth#acquireAccessToken(String, String)
   * @see MicrosoftAuth#checkEntitlementsMcStore(String)
   * @see MicrosoftAuth#acquireMinecraftProfile(String)
   */
  @Override
  public void run() {
    if (!MicrosoftAuthPanel.getInstance().isShowing()) {
      service.shutdown();
    }

    JSONObject consumersToken = MicrosoftAuth.acquireToken(clientId, deviceCode);
    if (Objects.isNull(consumersToken)) {
      return;
    }

    String[] tokenResponse = getTokenResponse(consumersToken);
    if (tokenResponse.length == 0) {
      return;
    }

    JSONObject userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
    if (Objects.isNull(userAuthenticate)) {
      return;
    }

    String[] xblTokenResponse = getXBLTokenResponse(userAuthenticate);
    if (xblTokenResponse.length == 0) {
      return;
    }

    JSONObject xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
    if (Objects.isNull(xstsAuthorize)) {
      return;
    }

    String xstsTokenResponse = getXSTSTokenResponse(xstsAuthorize);
    if (Objects.isNull(xstsTokenResponse)) {
      return;
    }

    JSONObject authenticateLoginWithXbox =
        MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    if (Objects.isNull(authenticateLoginWithXbox)) {
      return;
    }

    String[] accessTokenResponse = getAccessTokenResponse(authenticateLoginWithXbox);
    LauncherConfig.set(7, accessTokenResponse[0]);
    LauncherConfig.set(8, accessTokenResponse[1]);
    LauncherConfig.set(9, tokenResponse[1]);

    JSONObject entitlementsMcStore = MicrosoftAuth.checkEntitlementsMcStore(accessTokenResponse[0]);
    if (Objects.isNull(entitlementsMcStore)) {
      return;
    }

    boolean itemNameEqualToGameMinecraft = isItemNameEqualToGameMinecraft(entitlementsMcStore);
    if (!itemNameEqualToGameMinecraft) {
      LauncherConfig.set(5, null);
      LauncherConfig.set(6, null);
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(null, accessTokenResponse[0], null);
    } else {
      JSONObject minecraftProfile = MicrosoftAuth.acquireMinecraftProfile(accessTokenResponse[0]);
      if (Objects.isNull(minecraftProfile)) {
        return;
      }

      String[] minecraftProfileResponse = getMinecraftProfileResponse(minecraftProfile);
      LauncherConfig.set(5, minecraftProfileResponse[0]);
      LauncherConfig.set(6, minecraftProfileResponse[1]);
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(
          minecraftProfileResponse[1], accessTokenResponse[0], minecraftProfileResponse[0]);
    }
    service.shutdown();
  }
}
