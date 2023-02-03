package net.minecraft.util;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.ui.LauncherFrame;
import ee.twentyten.util.DebugLoggingManager;
import net.minecraft.authentication.mojang.YggdrasilAuthImpl;
import org.json.JSONException;
import org.json.JSONObject;

public class YggdrasilManager {

  public static final String YGGDRASIL_AUTH_URL;
  private static final YggdrasilAuthImpl YGGDRASIL_AUTH;

  static {
    YGGDRASIL_AUTH_URL = "https://authserver.mojang.com/%s";
    YGGDRASIL_AUTH = new YggdrasilAuthImpl();
  }

  public static JSONObject authenticateUser(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.authenticate(username, password, clientToken, requestUser);
    if (result.has("error")) {
      DebugLoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    DebugLoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static JSONObject validateAccessToken(String accessToken, String clientToken) {
    JSONObject result = YGGDRASIL_AUTH.validate(accessToken, clientToken);
    if (result.has("error")) {
      DebugLoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    DebugLoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static JSONObject refreshAccessToken(String accessToken, String clientToken,
      boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.refresh(accessToken, clientToken, requestUser);
    if (result.has("error")) {
      DebugLoggingManager.logError(YggdrasilManager.class, result.toString());
      return result;
    }

    DebugLoggingManager.logInfo(YggdrasilManager.class, result.toString());
    return result;
  }

  public static void checkYggdrasilSession(String accessToken, String clientToken) {
    JSONObject session = YggdrasilManager.validateAccessToken(accessToken, clientToken);
    if (session.has("error")) {
      session = YggdrasilManager.refreshAccessToken(accessToken, clientToken, true);
      if (session.has("error")) {
        throw new JSONException("Failed to validate or refresh session");
      }
      String newAccessToken = session.getString("accessToken");
      String newClientToken = session.getString("clientToken");
      Config.instance.setAccessToken(newAccessToken);
      Config.instance.setClientToken(newClientToken);
      Config.instance.save();
    } else {
      LauncherFrame.sessionValid = true;
    }
  }
}
