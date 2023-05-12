package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuth;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuthWorker;
import java.util.Objects;
import lombok.val;

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

    LauncherConfig.lookup.put("mojangUsername", username);
    LauncherConfig.lookup.put("mojangPassword", rememberPasswordChecked ? password : "");
    LauncherConfig.lookup.put("mojangRememberPasswordChecked", rememberPasswordChecked);

    new YggdrasilAuthWorker(username, password, clientToken).execute();
  }

  public static boolean isAccessTokenExpired() {
    val accessToken = (String) LauncherConfig.lookup.get("mojangAccessToken");
    val clientToken = (String) LauncherConfig.lookup.get("mojangClientToken");
    if (Objects.isNull(accessToken) || Objects.isNull(clientToken)) {
      return false;
    }
    if (accessToken.isEmpty() || clientToken.isEmpty()) {
      return false;
    }

    val validate = YggdrasilAuth.validateAccessToken(accessToken, clientToken);
    Objects.requireNonNull(validate, "validate cannot be null");
    return validate.has("error");
  }

  public static void refreshAccessToken() {
    val accessToken = (String) LauncherConfig.lookup.get("mojangAccessToken");
    val clientToken = (String) LauncherConfig.lookup.get("mojangClientToken");

    val refresh = YggdrasilAuth.refreshAccessToken(accessToken, clientToken);
    Objects.requireNonNull(refresh, "refresh cannot be null");
    if (refresh.has("error")) {
      throw new RuntimeException("Failed to refresh access token");
    }

    val newAccessToken = refresh.getString("accessToken");
    LauncherConfig.lookup.put("mojangAccessToken", newAccessToken);
    LauncherConfig.saveConfig();
  }
}
