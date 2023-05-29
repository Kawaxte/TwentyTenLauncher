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

import ch.kawaxte.launcher.ui.LauncherNoNetworkPanel;
import ch.kawaxte.launcher.ui.LauncherPanel;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.GenericData;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing a range of static methods to handle different stages of the OAuth2 flow,
 * including acquiring and refreshing tokens, making API requests to Xbox Live and Minecraft
 * services, and retrieving the user's Minecraft profile.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 * @see <a
 *     href="https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-device-code">
 *     OAuth 2.0 device authorization grant</a>
 */
public final class MicrosoftAuth {

  private static final Logger LOGGER;

  static {
    LOGGER = LoggerFactory.getLogger(MicrosoftAuth.class);
  }

  private MicrosoftAuth() {}

  /**
   * Initialises and returns URLs for Microsoft OAuth2 authentication process.
   *
   * @return Array of URLs necessary for the OAuth2 flow
   */
  private static GenericUrl[] getGenericUrls() {
    GenericUrl[] urls = new GenericUrl[7];
    urls[0] =
        new GenericUrl(
            new StringBuilder()
                .append("https://login.microsoftonline.com/")
                .append("consumers/")
                .append("oauth2/")
                .append("v2.0/")
                .append("devicecode")
                .toString());
    urls[1] =
        new GenericUrl(
            new StringBuilder()
                .append("https://login.microsoftonline.com/")
                .append("consumers/")
                .append("oauth2/")
                .append("v2.0/")
                .append("token")
                .toString());
    urls[2] =
        new GenericUrl(
            new StringBuilder()
                .append("https://user.auth.xboxlive.com/")
                .append("user/")
                .append("authenticate")
                .toString());
    urls[3] =
        new GenericUrl(
            new StringBuilder()
                .append("https://xsts.auth.xboxlive.com/")
                .append("xsts/")
                .append("authorize")
                .toString());
    urls[4] =
        new GenericUrl(
            new StringBuilder()
                .append("https://api.minecraftservices.com/")
                .append("authentication/")
                .append("login_with_xbox")
                .toString());
    urls[5] =
        new GenericUrl(
            new StringBuilder()
                .append("https://api.minecraftservices.com/")
                .append("entitlements/")
                .append("mcstore")
                .toString());
    urls[6] =
        new GenericUrl(
            new StringBuilder()
                .append("https://api.minecraftservices.com/")
                .append("minecraft/")
                .append("profile")
                .toString());
    return urls;
  }

  /**
   * Acquires a device code that will be used to retrieve both the user's access token and refresh
   * token.
   *
   * <p>This method is required to be polled every second until the user has entered the code on the
   * provided URL. The polling process is handled by {@link MicrosoftAuthWorker}
   *
   * @param clientId The client ID of the Azure application requesting the device code
   * @return A {@link JSONObject} containing the response from the server
   */
  public static JSONObject acquireDeviceCode(String clientId) {
    GenericData data = new GenericData();
    data.put("client_id", clientId);
    data.put("response_type", "code");
    data.put("scope", "XboxLive.signin offline_access");

    HttpTransport transport = new NetHttpTransport();
    HttpContent content = new UrlEncodedContent(data);

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[0], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire device code", ioe);
    }
    return null;
  }

  /**
   * Acquires both the user's access token and refresh token using the obtained device code.
   * received.
   *
   * <p>This method should be called after a successful call to {@link #acquireDeviceCode(String)}.
   *
   * @param clientId The client ID of the Azure application requesting the access token
   * @param deviceCode The device code received from {@link #acquireDeviceCode(String)}
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireToken(String clientId, String deviceCode) {
    GenericData data = new GenericData();
    data.put("client_id", clientId);
    data.put("device_code", deviceCode);
    data.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

    HttpTransport transport = new NetHttpTransport();
    HttpContent content = new UrlEncodedContent(data);

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[1], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire access token", ioe);
    }
    return null;
  }

  /**
   * Refreshes the user's access token and refresh token using a previously obtained refresh token.
   *
   * <p>Upon a successful refresh, the new access token and refresh token will be returned in the
   * response, which can then be used to re-obtain the Minecraft access token.
   *
   * @param clientId The client ID of the Azure application requesting the access token
   * @param refreshToken The refresh token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject refreshToken(String clientId, String refreshToken) {
    GenericData data = new GenericData();
    data.put("client_id", clientId);
    data.put("refresh_token", refreshToken);
    data.put("grant_type", "refresh_token");

    HttpTransport transport = new NetHttpTransport();
    HttpContent content = new UrlEncodedContent(data);

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[1], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot refresh access token", ioe);
    }
    return null;
  }

  /**
   * Acquires the user's Xbox Live token using an previously obtained access token.
   *
   * @param accessToken The access token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireXBLToken(String accessToken) {
    JSONObject properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", String.format("d=%s", accessToken));

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "http://auth.xboxlive.com");
    data.put("TokenType", "JWT");

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", data.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[2], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire Xbox Live token", ioe);
    }
    return null;
  }

  /**
   * Acquires the user's Xbox Live XSTS token using the previously obtained Xbox Live token.
   *
   * @param token The Xbox Live token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireXSTSToken(String token) {
    JSONObject properties = new JSONObject();
    properties.put("SandboxId", "RETAIL");
    properties.put("UserTokens", new String[] {token});

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "rp://api.minecraftservices.com/");
    data.put("TokenType", "JWT");

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", data.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[3], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire XSTS token", ioe);
    }
    return null;
  }

  /**
   * Acquires the user's Minecraft access token using the previously obtained XSTS token.
   *
   * <p>The Minecraft access token will not only be used to construct a valid session ID, but also
   * to retrieve the user's profile, and to check if the user owns the minecraft.
   *
   * @param uhs The user hash
   * @param token The XSTS token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireAccessToken(String uhs, String token) {
    JSONObject body = new JSONObject();
    body.put("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));
    body.put("ensureLegacyEnabled", true);

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", body.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[4], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire Minecraft access token", ioe);
    }
    return null;
  }

  /**
   * Checks if the user owns Minecraft (Java Edition) using the previously obtained Minecraft access
   * token. received. If not, the user will only be able to play in an instance where their username
   * is randomly generated in a "Player###" format, and they will not be able to join multiplayer
   * servers that have {@code online-mode} enabled.
   *
   * @param accessToken The Minecraft access token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject checkEntitlementsMcStore(String accessToken) {
    HttpTransport transport = new NetHttpTransport();

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildGetRequest(getGenericUrls()[5]);
      request.getHeaders().setAuthorization(String.format("Bearer %s", accessToken));

      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot check Minecraft Store entitlements", ioe);
    }
    return null;
  }

  /**
   * Acquires the user's Minecraft profile using the previously obtained Minecraft access token.
   *
   * <p>The Minecraft profile will be used to retrieve the user's profile information, such as their
   * username, UUID, skin, etc. The UUID will be used as one of the components to construct a valid
   * session ID.
   *
   * @param accessToken The Minecraft access token of the user
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireMinecraftProfile(String accessToken) {
    HttpTransport transport = new NetHttpTransport();

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildGetRequest(getGenericUrls()[6]);
      request.getHeaders().setAuthorization(String.format("Bearer %s", accessToken));

      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      return null;
    } catch (IOException ioe) {
      LOGGER.error("Cannot acquire Minecraft profile", ioe);
    }
    return null;
  }
}
