package io.github.kawaxte.twentyten.launcher.util;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;

import io.github.kawaxte.twentyten.launcher.auth.AbstractYggdrasilAuthImpl;
import io.github.kawaxte.twentyten.launcher.auth.YggdrasilAuthTask;
import lombok.val;
import org.json.JSONObject;

public final class YggdrasilAuthUtils {

  public static AbstractYggdrasilAuthImpl authInstance;

  static {
    authInstance = new AbstractYggdrasilAuthImpl();
  }

  private YggdrasilAuthUtils() {}

  public static void runYggdrasilAuthTask(
      String username, String password, String clientToken, boolean rememberPasswordChecked) {
    configInstance.setMojangUsername(username);
    configInstance.setMojangPassword(rememberPasswordChecked ? password : "");
    configInstance.setMojangRememberPasswordChecked(rememberPasswordChecked);

    new YggdrasilAuthTask(username, password, clientToken).run();
  }

  public static void validateAndRefreshAccessToken() {
    if (configInstance.getMojangAccessToken().isEmpty()
        || configInstance.getMojangClientToken().isEmpty()) {
      return;
    }

    val validate =
        authInstance.validateAccessToken(
            configInstance.getMojangAccessToken(), configInstance.getMojangClientToken());
    if (isValidateAccessTokenErrored(validate)) {
      return;
    }

    val refresh =
        authInstance.refreshAccessToken(
            configInstance.getMojangAccessToken(), configInstance.getMojangClientToken());
    if (isRefreshAccessTokenErrored(refresh)) {
      throw new RuntimeException("Failed to refresh access token");
    }

    val accessToken = refresh.getString("accessToken");
    configInstance.setMojangAccessToken(accessToken);
    configInstance.save();
  }

  private static boolean isRefreshAccessTokenErrored(JSONObject object) {
    return !object.has("error");
  }

  private static boolean isValidateAccessTokenErrored(JSONObject object) {
    return !object.has("error");
  }
}
