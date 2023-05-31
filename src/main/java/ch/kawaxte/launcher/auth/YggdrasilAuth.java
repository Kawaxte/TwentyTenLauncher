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

import ch.kawaxte.launcher.LauncherConfig;
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
import com.google.api.client.http.javanet.NetHttpTransport;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing a range of static methods to authenticate with Mojang (Yggdrasil) API.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0923_03
 */
public final class YggdrasilAuth {

  private static final Logger LOGGER;

  static {
    LOGGER = LoggerFactory.getLogger(YggdrasilAuth.class);
  }

  private YggdrasilAuth() {}

  /**
   * Initialises and returns the URLs for Mojang API.
   *
   * @return An array of URLs for use in authenticating with a Legacy or Mojang account
   */
  private static GenericUrl[] getGenericUrls() {
    GenericUrl[] authUrls = new GenericUrl[3];
    authUrls[0] =
        new GenericUrl(
            new StringBuilder()
                .append("https://authserver.mojang.com/")
                .append("authenticate")
                .toString());
    authUrls[1] =
        new GenericUrl(
            new StringBuilder()
                .append("https://authserver.mojang.com/")
                .append("validate")
                .toString());
    authUrls[2] =
        new GenericUrl(
            new StringBuilder()
                .append("https://authserver.mojang.com/")
                .append("refresh")
                .toString());
    return authUrls;
  }

  /**
   * Authenticates with Mojang API, used to retrieve partial profile information.
   *
   * @param username username of the account (or email address if Mojang account)
   * @param password password of the account
   * @param clientToken a random UUID (version 4) generated by the launcher
   * @return A JSON object containing the user's access token, client token, and partial profile
   *     information
   */
  public static JSONObject authenticate(String username, String password, String clientToken) {
    JSONObject agent = new JSONObject();
    agent.put("name", "Minecraft");
    agent.put("version", 1);

    JSONObject data = new JSONObject();
    data.put("agent", agent);
    data.put("username", username);
    data.put("password", password);
    data.put("clientToken", clientToken);
    data.put("requestUser", true);

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", data.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[0], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[1], uhe.getMessage()));
      LauncherUtils.setNotPremium(false);
    } catch (IOException ioe) {
      LauncherUtils.swapContainers(
          LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[0]));
      LauncherUtils.setNotPremium(true);

      LOGGER.error("Cannot authenticate with Mojang", ioe);
    }
    return null;
  }

  /**
   * Refreshes the access token of the user using the previously obtained access token and generated
   * client token.
   *
   * @param accessToken The access token of the user
   * @param clientToken The client token generated by the launcher on first launch
   * @return An empty JSON object if the access token is valid and can be refreshed, or a JSON
   *     object containing the error message if the access token is invalid or expired
   */
  public static JSONObject validate(String accessToken, String clientToken) {
    JSONObject data = new JSONObject();
    data.put("accessToken", accessToken);
    data.put("clientToken", clientToken);

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", data.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[1], content);
      HttpResponse response = request.execute();
      return response.parseAsString().isEmpty()
          ? new JSONObject()
          : new JSONObject(response.parseAsString());
    } catch (IOException ioe) {
      JSONObject refresh = refresh(accessToken, clientToken);
      if (Objects.isNull(refresh)) {
        return null;
      }

      String newAccessToken = refresh.getString("accessToken");
      LauncherConfig.set(17, newAccessToken);
      LauncherConfig.saveConfig();
    }
    return null;
  }

  /**
   * Refreshes the access token of the user using the previously obtained access token and generated
   * client token.
   *
   * <p>Unlike {@link #validate(String, String)}, this method will throw an exception if the access
   * token could not be refreshed.
   *
   * @param accessToken the access token of the user
   * @param clientToken the client token generated by the launcher on first launch
   * @return A JSON object containing the new access token and client token
   */
  public static JSONObject refresh(String accessToken, String clientToken) {
    JSONObject data = new JSONObject();
    data.put("accessToken", accessToken);
    data.put("clientToken", clientToken);
    data.put("requestUser", true);

    HttpTransport transport = new NetHttpTransport();
    HttpContent content =
        new ByteArrayContent("application/json", data.toString().getBytes(StandardCharsets.UTF_8));

    HttpRequestFactory factory = transport.createRequestFactory();
    try {
      HttpRequest request = factory.buildPostRequest(getGenericUrls()[2], content);
      HttpResponse response = request.execute();
      return new JSONObject(response.parseAsString());
    } catch (UnknownHostException uhe) {
      return null; // Why would we want to refresh without internet connection?
    } catch (IOException ioe) {
      LOGGER.error("Cannot refresh access token", ioe);
    }
    return null;
  }
}
