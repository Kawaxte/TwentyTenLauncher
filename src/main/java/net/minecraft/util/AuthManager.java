package net.minecraft.util;

import ee.twentyten.util.DebugLoggingManager;
import net.minecraft.auth.yggdrasil.YggdrasilAuthImpl;
import org.json.JSONObject;

public class AuthManager {

  public static final String YGGDRASIL_AUTH_URL;
  private static final YggdrasilAuthImpl YGGDRASIL_AUTH;

  static {
    YGGDRASIL_AUTH_URL = "https://authserver.mojang.com/%s";
    YGGDRASIL_AUTH = new YggdrasilAuthImpl();
  }

  public static JSONObject authenticateWithYggdrasil(String username, String password,
      String clientToken, boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.authenticate(username, password, clientToken, requestUser);

    DebugLoggingManager.logInfo(AuthManager.class, result.toString());
    return result;
  }

  public static JSONObject refreshWithYggdrasil(String accessToken, String clientToken,
      boolean requestUser) {
    JSONObject result = YGGDRASIL_AUTH.refresh(accessToken, clientToken, requestUser);

    DebugLoggingManager.logInfo(AuthManager.class, result.toString());
    return result;
  }

  public static JSONObject validateWithYggdrasil(String accessToken, String clientToken) {
    JSONObject result = YGGDRASIL_AUTH.validate(accessToken, clientToken);

    DebugLoggingManager.logInfo(AuthManager.class, result.toString());
    return result;
  }
}
