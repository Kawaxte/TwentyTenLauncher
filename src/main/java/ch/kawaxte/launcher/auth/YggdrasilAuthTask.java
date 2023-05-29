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
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Task performing the authentication process when run, sending the necessary credentials and
 * handling the response. The response may indicate an error, a successful login without the
 * Minecraft profile, or a successful login with the Minecraft profile.
 *
 * <p>Upon receiving a response, the task updates the {@link LauncherConfig} with newly obtained
 * properties and initiates the Minecraft launch process with the appropriate parameters based on
 * the response.
 *
 * @see Runnable
 * @author Kawaxte
 * @since 1.5.0923_03
 * @see YggdrasilAuth
 * @see YggdrasilAuthWorker
 */
public class YggdrasilAuthTask implements Runnable {

  private final String username;
  private final String password;
  private final String clientToken;

  /**
   * Constructs a new authentication task with the given parameters.
   *
   * @param username username of the account (or email address if Mojang account)
   * @param password password of the account
   * @param clientToken a random UUID (version 4) generated by the launcher
   */
  public YggdrasilAuthTask(String username, String password, String clientToken) {
    this.username = username;
    this.password = password;
    this.clientToken = clientToken;
  }

  /**
   * Returns whether the selected profile is a legacy profile.
   *
   * @param object the JSON object containing the response from the authentication request
   * @return {@code true} if the selected profile is a legacy profile, {@code false} otherwise
   */
  private boolean isLegacyInSelectedProfile(JSONObject object) {
    JSONObject selectedProfile = object.getJSONObject("selectedProfile");
    return selectedProfile.has("legacy") && selectedProfile.getBoolean("legacy");
  }

  /**
   * Returns whether the available profiles array is empty. This is to check if the account owns
   * Minecraft (Java Edition).
   *
   * @param object the JSON object containing the response from the authentication request
   * @return {@code true} if the available profiles array is empty, {@code false} otherwise
   */
  private boolean isAvailableProfilesEmpty(JSONObject object) {
    JSONArray availableProfiles = object.getJSONArray("availableProfiles");
    return object.has("availableProfiles") && availableProfiles.isEmpty();
  }

  /**
   * Returns the authentication response as an array of strings.
   *
   * @param object the JSON object containing the response from the authentication request
   * @return {@code accessToken}, {@code id}, and {@code name} as an array of strings
   */
  private String[] getAuthenticateResponse(JSONObject object) {
    String accessToken = object.getString("accessToken");
    if (object.has("selectedProfile")) {
      JSONObject selectedProfile = object.getJSONObject("selectedProfile");
      String id = selectedProfile.getString("id");
      String name = selectedProfile.getString("name");
      return new String[] {accessToken, id, name};
    }
    return new String[] {accessToken, null, null};
  }

  /**
   * Performs the Yggdrasil authentication process using the supplied credentials. Upon receiving a
   * response, it updates the launcher configuration with the new session information.
   *
   * <p>If an error is received or the account does not own Minecraft (Java Edition), it will launch
   * the minecraft with a generic player name. If the account does own Minecraft, it will launch the
   * minecraft with the authenticated user's profile.
   *
   * @see YggdrasilAuth#authenticate(String, String, String)
   */
  @Override
  public void run() {
    JSONObject authenticate = YggdrasilAuth.authenticate(username, password, clientToken);
    if (Objects.isNull(authenticate)) {
      return;
    }

    String[] authenticateResponse = this.getAuthenticateResponse(authenticate);
    LauncherConfig.set(17, authenticateResponse[0]);

    if (this.isAvailableProfilesEmpty(authenticate)) {
      LauncherConfig.set(14, null);
      LauncherConfig.set(15, null);
      LauncherConfig.set(16, false);
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(null, authenticateResponse[0], null);
    } else {
      LauncherConfig.set(14, authenticateResponse[1]);
      LauncherConfig.set(15, authenticateResponse[2]);
      LauncherConfig.set(16, this.isLegacyInSelectedProfile(authenticate));
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(
          authenticateResponse[2], authenticateResponse[0], authenticateResponse[1]);
    }
  }
}
