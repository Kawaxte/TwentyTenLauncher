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

import io.github.kawaxte.twentyten.launcher.Launcher;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.Objects;
import lombok.val;
import org.json.JSONObject;

public class YggdrasilAuthTask implements Runnable {

  private final String username;
  private final String password;
  private final String clientToken;

  public YggdrasilAuthTask(String username, String password, String clientToken) {
    this.username = username;
    this.password = password;
    this.clientToken = clientToken;
  }

  @Override
  public void run() {
    val authenticate = YggdrasilAuth.authenticate(username, password, clientToken);
    if (Objects.isNull(authenticate)) {
      return;
    }
    val authenticateResponse = this.getAuthenticateResponse(authenticate);
    if (Objects.isNull(authenticateResponse)) {
      return;
    }

    LauncherConfig.lookup.put("mojangAccessToken", authenticateResponse[0]);

    if (this.isAvailableProfilesEmpty(authenticate)) {
      LauncherConfig.lookup.put("mojangProfileId", null);
      LauncherConfig.lookup.put("mojangProfileName", null);
      LauncherConfig.lookup.put("mojangProfileLegacy", false);
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(null, authenticateResponse[0], null);
    } else {
      LauncherConfig.lookup.put("mojangProfileId", authenticateResponse[1]);
      LauncherConfig.lookup.put("mojangProfileName", authenticateResponse[2]);
      LauncherConfig.lookup.put(
          "mojangProfileLegacy", this.isLegacyInSelectedProfile(authenticate));
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(
          authenticateResponse[2], authenticateResponse[0], authenticateResponse[1]);
    }
  }

  private boolean isLegacyInSelectedProfile(JSONObject object) {
    val selectedProfile = object.getJSONObject("selectedProfile");
    return selectedProfile.has("legacy") && selectedProfile.getBoolean("legacy");
  }

  private boolean isAvailableProfilesEmpty(JSONObject object) {
    return object.getJSONArray("availableProfiles").isEmpty();
  }

  private String[] getAuthenticateResponse(JSONObject object) {
    if (object.has("error")) {
      LauncherUtils.swapContainers(
          LauncherPanel.instance, new LauncherNoNetworkPanel("lnnp.errorLabel.signin"));
      return null;
    }

    val accessToken = object.getString("accessToken");
    val selectedProfile = object.getJSONObject("selectedProfile");
    val id = selectedProfile.getString("id");
    val name = selectedProfile.getString("name");
    return new String[] {accessToken, id, name};
  }
}
