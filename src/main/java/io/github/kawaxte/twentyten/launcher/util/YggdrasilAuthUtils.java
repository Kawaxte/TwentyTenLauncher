package io.github.kawaxte.twentyten.launcher.util;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.auth.AbstractYggdrasilAuthImpl;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuthWorker;
import java.util.Objects;
import lombok.val;
import org.json.JSONObject;

public final class YggdrasilAuthUtils {

  public static AbstractYggdrasilAuthImpl authInstance;

  static {
    authInstance = new AbstractYggdrasilAuthImpl();
  }

  private YggdrasilAuthUtils() {}

  public static void executeYggdrasilAuthWorker(
      String username, String password, String clientToken, boolean rememberPasswordChecked) {
    if ((Objects.isNull(username) || Objects.isNull(password) || Objects.isNull(clientToken))
        || username.isEmpty()
        || password.isEmpty()
        || clientToken.isEmpty()) {
      return;
    }

    LauncherConfig.lookup.put("mojangUsername", username);
    LauncherConfig.lookup.put("mojangPassword", rememberPasswordChecked ? password : "");
    LauncherConfig.lookup.put("mojangRememberPasswordChecked", rememberPasswordChecked);

    new YggdrasilAuthWorker(username, password, clientToken).execute();
  }

  public static void validateAndRefreshAccessToken() {
    val accessToken = (String) LauncherConfig.lookup.get("mojangAccessToken");
    val clientToken = (String) LauncherConfig.lookup.get("mojangClientToken");
    if ((Objects.isNull(accessToken)
        || Objects.isNull(clientToken)
        || accessToken.isEmpty()
        || clientToken.isEmpty())) {
      return;
    }

    val validate = authInstance.validateAccessToken(accessToken, clientToken);
    System.out.println(validate);
    if (isValidateAccessTokenErrored(validate)) {
      return;
    }

    val refresh = authInstance.refreshAccessToken(accessToken, clientToken);
    System.out.println(refresh);
    if (isRefreshAccessTokenErrored(refresh)) {
      throw new RuntimeException("Failed to refresh access token");
    }

    val newAccessToken = refresh.getString("accessToken");
    LauncherConfig.lookup.put("mojangAccessToken", newAccessToken);
    LauncherConfig.saveConfig();
  }

  private static boolean isRefreshAccessTokenErrored(JSONObject object) {
    return !object.has("error");
  }

  private static boolean isValidateAccessTokenErrored(JSONObject object) {
    return !object.has("error");
  }
}
