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

package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * This class provides a range of static methods to handle different stages of the OAuth2 flow,
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
    LOGGER = LogManager.getLogger(MicrosoftAuth.class);
  }

  private MicrosoftAuth() {}

  /**
   * Initialises and returns URLs for Microsoft OAuth2 authentication process.
   *
   * @return Array of URLs necessary for the OAuth2 flow
   */
  private static URL[] getYggdrasilAuthUrls() {
    URL[] urls = new URL[7];
    try {
      urls[0] =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("devicecode")
                  .toString());
      urls[1] =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("token")
                  .toString());
      urls[2] =
          new URL(
              new StringBuilder()
                  .append("https://user.auth.xboxlive.com/")
                  .append("user/")
                  .append("authenticate")
                  .toString());
      urls[3] =
          new URL(
              new StringBuilder()
                  .append("https://xsts.auth.xboxlive.com/")
                  .append("xsts/")
                  .append("authorize")
                  .toString());
      urls[4] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("authentication/")
                  .append("login_with_xbox")
                  .toString());
      urls[5] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("entitlements/")
                  .append("mcstore")
                  .toString());
      urls[6] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("minecraft/")
                  .append("profile")
                  .toString());
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL for Microsoft API", murle);
    }
    return urls;
  }

  /**
   * Acquires a device code that will be used to retrieve both the user's access token and refresh
   * token.
   *
   * <p>This method is required to be polled every second until the user has entered the code on the
   * provided URL. The polling process is handled by {@link
   * io.github.kawaxte.twentyten.launcher.auth.MicrosoftAuthWorker}
   *
   * @param clientId The client ID of the Azure application requesting the device code
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireDeviceCode(String clientId) {
    Form body = Form.form();
    body.add("client_id", clientId);
    body.add("response_type", "code");
    body.add("scope", "XboxLive.signin offline_access");

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[0].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring device code", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring device code", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Acquires both the user's access token and refresh token using the obtained device code.
   * received.
   *
   * <p>This method should be called after a successful call to {@link #acquireDeviceCode(String)}.
   *
   * @param clientId The client ID of the Azure application requesting the access token.
   * @param deviceCode The device code previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireToken(String clientId, String deviceCode) {
    Form body = Form.form();
    body.add("client_id", clientId);
    body.add("device_code", deviceCode);
    body.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring token", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Refreshes the user's access token and refresh token using a previously obtained refresh token.
   *
   * <p>Upon a successful refresh, the new access token and refresh token will be returned in the
   * response, which can then be used to re-obtain the Minecraft access token.
   *
   * @param clientId The client ID of the Azure application requesting the access token.
   * @param refreshToken The refresh token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject refreshToken(String clientId, String refreshToken) {
    Form body = Form.form();
    body.add("client_id", clientId);
    body.add("refresh_token", refreshToken);
    body.add("grant_type", "refresh_token");

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while refreshing token", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        return null;
      }

      LOGGER.error("Error while refreshing token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Acquires the user's Xbox Live token using an previously obtained access token.
   *
   * @param accessToken The access token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireXBLToken(String accessToken) {
    JSONObject properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", String.format("d=%s", accessToken));

    JSONObject body = new JSONObject();
    body.put("Properties", properties);
    body.put("RelyingParty", "http://auth.xboxlive.com");
    body.put("TokenType", "JWT");

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[2].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring XBL token", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring XBL token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Acquires the user's Xbox Live XSTS token using the previously obtained Xbox Live token.
   *
   * @param token The Xbox Live token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireXSTSToken(String token) {
    JSONObject properties = new JSONObject();
    properties.put("SandboxId", "RETAIL");
    properties.put("UserTokens", new String[] {token});

    JSONObject body = new JSONObject();
    body.put("Properties", properties);
    body.put("RelyingParty", "rp://api.minecraftservices.com/");
    body.put("TokenType", "JWT");

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[3].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring XSTS token", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring XSTS token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Acquires the user's Minecraft access token using the previously obtained XSTS token.
   *
   * <p>The Minecraft access token will not only be used to construct a valid session ID, but also
   * to retrieve the user's profile, and to check if the user owns the game.
   *
   * @param uhs The user hash
   * @param token The XSTS token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireAccessToken(String uhs, String token) {
    JSONObject body = new JSONObject();
    body.put("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));
    body.put("ensureLegacyEnabled", true);

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[4].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring access token", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring access token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  /**
   * Checks if the user owns Minecraft (Java Edition) using the previously obtained Minecraft access
   * token. received. If not, the user will only be able to play in an instance where their username
   * is randomly generated in a "Player###" format, and they will not be able to join multiplayer
   * servers that have {@code online-mode} enabled.
   *
   * @param accessToken The Minecraft access token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject checkEntitlementsMcStore(String accessToken) {
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.get(getYggdrasilAuthUrls()[5].toURI())
                    .addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring items", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring items", cause);
    } finally {
      service.shutdown();
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
   * @param accessToken The Minecraft access token previously obtained
   * @return A {@link org.json.JSONObject} containing the response from the server
   */
  public static JSONObject acquireMinecraftProfile(String accessToken) {
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.get(getYggdrasilAuthUrls()[6].toURI())
                    .addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while checking Minecraft profile", ie);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while checking Minecraft profile", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
