package com.mojang.util;

import com.mojang.YggdrasilAuthenticationImpl;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.LogHelper;
import org.json.JSONObject;

public final class YggdrasilHelper {

  public static final String YGGDRASIL_AUTH_URL;
  private static final YggdrasilAuthenticationImpl AUTHENTICATION_IMPL;
  private static final Class<YggdrasilHelper> CLASS_REF;

  static {
    YGGDRASIL_AUTH_URL = "https://authserver.mojang.com/%s";
    AUTHENTICATION_IMPL = new YggdrasilAuthenticationImpl();

    CLASS_REF = YggdrasilHelper.class;
  }

  public static JSONObject authenticate(String username, String password,
      String clientToken,
      boolean requestUser) {
    JSONObject result = AUTHENTICATION_IMPL.authenticate(username, password, clientToken,
        requestUser);
    if (result.has("error")) {
      LogHelper.logError(CLASS_REF, result.toString());
      return result;
    }

    LogHelper.logInfo(CLASS_REF, result.toString());
    return result;
  }

  public static JSONObject validate(String accessToken, String clientToken) {
    JSONObject result = AUTHENTICATION_IMPL.validate(accessToken, clientToken);
    if (result.has("error")) {
      LogHelper.logError(CLASS_REF, result.toString());
      return result;
    }

    LogHelper.logInfo(CLASS_REF, result.toString());
    return result;
  }

  public static JSONObject refresh(String accessToken, String clientToken,
      boolean requestUser) {
    JSONObject result = AUTHENTICATION_IMPL.refresh(accessToken, clientToken, requestUser);
    if (result.has("error")) {
      LogHelper.logError(CLASS_REF, result.toString());
      return result;
    }

    LogHelper.logInfo(CLASS_REF, result.toString());
    return result;
  }

  public static boolean isSessionExpired(String accessToken, String clientToken) {
    JSONObject validate = YggdrasilHelper.validate(accessToken, clientToken);
    if (validate.has("error")) {
      JSONObject refresh = YggdrasilHelper.refresh(accessToken, clientToken, true);
      if (refresh.has("error")) {
        LogHelper.logError(CLASS_REF, "Failed to refresh access token");
        return false;
      }
      String newAccessToken = refresh.getString("accessToken");
      String newClientToken = refresh.getString("clientToken");
      LauncherConfig.instance.setAccessToken(newAccessToken);
      LauncherConfig.instance.setClientToken(newClientToken);
      LauncherConfig.instance.save();
    }
    return true;
  }
}
