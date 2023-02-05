package net.minecraft.util;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.ui.LauncherFrame;
import ee.twentyten.util.LoggingManager;
import net.minecraft.authentication.mojang.YggdrasilAuthImpl;
import org.json.JSONObject;

public class YggdrasilManager {

  public static final String YGGDRASIL_AUTH_URL;
  private static final YggdrasilAuthImpl YGGDRASIL_AUTH;

  static {
    YGGDRASIL_AUTH_URL = "https://authserver.mojang.com/%s";
    YGGDRASIL_AUTH = new YggdrasilAuthImpl();
  }

  public static JSONObject login(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.authenticate(username, password, clientToken, requestUser);
    if (result.has("error")) {
      LoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    LauncherFrame.sessionValid = true;

    LoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static JSONObject validateAccessToken(String accessToken, String clientToken) {
    JSONObject result = YGGDRASIL_AUTH.validate(accessToken, clientToken);
    if (result.has("error")) {
      LoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    LoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static JSONObject refreshAccessToken(String accessToken, String clientToken,
      boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.refresh(accessToken, clientToken, requestUser);
    if (result.has("error")) {
      LoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    LoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static void checkYggdrasilSession(String accessToken, String clientToken) {
    JSONObject validate = YggdrasilManager.validateAccessToken(accessToken, clientToken);
    if (validate.has("error")) {
      JSONObject refresh = YggdrasilManager.refreshAccessToken(accessToken, clientToken, true);
      if (refresh.has("error")) {
        LauncherFrame.sessionValid = false;

        LoggingManager.logError(YggdrasilManager.class, "Failed to refresh access token");
        return;
      }
      String newAccessToken = refresh.getString("accessToken");
      String newClientToken = refresh.getString("clientToken");
      Config.instance.setAccessToken(newAccessToken);
      Config.instance.setClientToken(newClientToken);
      Config.instance.save();
    }
    LauncherFrame.sessionValid = true;
  }
}
