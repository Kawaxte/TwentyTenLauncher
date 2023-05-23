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

import io.github.kawaxte.twentyten.exception.AuthenticationException;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuth;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuthWorker;
import java.util.Objects;
import org.json.JSONObject;

public final class YggdrasilAuthUtils {

  private YggdrasilAuthUtils() {}

  public static void executeYggdrasilAuthWorker(
      String username, String password, String clientToken, boolean rememberPasswordChecked) {
    if ((Objects.isNull(username) || Objects.isNull(password) || Objects.isNull(clientToken))) {
      throw new NullPointerException("username, password, or clientToken cannot be null");
    }
    if (clientToken.isEmpty()) {
      throw new IllegalArgumentException("clientToken cannot be empty");
    }

    LauncherConfig.set(11, username);
    LauncherConfig.set(12, rememberPasswordChecked ? password : "");
    LauncherConfig.set(13, rememberPasswordChecked);

    new YggdrasilAuthWorker(username, password, clientToken).execute();
  }

  public static boolean isAccessTokenExpired() {
    String accessToken = (String) LauncherConfig.get(17);
    String clientToken = (String) LauncherConfig.get(18);
    if (Objects.isNull(accessToken) || Objects.isNull(clientToken)) {
      return false;
    }
    if (accessToken.isEmpty() || clientToken.isEmpty()) {
      return false;
    }

    JSONObject validate = YggdrasilAuth.validateAccessToken(accessToken, clientToken);
    if (Objects.isNull(validate)) {
      return false;
    }
    return validate.has("error");
  }

  public static void refreshAccessToken() {
    String accessToken = (String) LauncherConfig.get(17);
    String clientToken = (String) LauncherConfig.get(18);

    JSONObject refresh = YggdrasilAuth.refreshAccessToken(accessToken, clientToken);
    if (Objects.isNull(refresh)) {
      return;
    }
    if (refresh.has("error")) {
      throw new AuthenticationException("Cannot refresh access token");
    }

    String newAccessToken = refresh.getString("accessToken");
    LauncherConfig.set(17, newAccessToken);
    LauncherConfig.saveConfig();
  }
}
