package com.mojang.util;

import com.mojang.YggdrasilAuthenticationImpl;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.util.LoggerHelper;
import org.json.JSONObject;

public final class YggdrasilHelper {

  public static final String YGGDRASIL_AUTH_URL;
  private static final YggdrasilAuthenticationImpl AUTHENTICATION_IMPL;

  static {
    YGGDRASIL_AUTH_URL = "https://authserver.mojang.com/%s";
    AUTHENTICATION_IMPL = new YggdrasilAuthenticationImpl();
  }

  public static JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject result = AUTHENTICATION_IMPL.authenticate(username, password, clientToken,
        requestUser);
    if (result.has("error")) {
      LoggerHelper.logError(result.toString(), false);
      return result;
    }

    LoggerHelper.logInfo(result.toString(), false);
    return result;
  }

  public static JSONObject validate(String accessToken, String clientToken) {
    JSONObject result = AUTHENTICATION_IMPL.validate(accessToken, clientToken);
    if (result == null) {
      return new JSONObject();
    }
    if (result.has("error")) {
      LoggerHelper.logError(result.toString(), false);
      return result;
    }

    LoggerHelper.logInfo(result.toString(), false);
    return result;
  }

  public static JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    JSONObject result = AUTHENTICATION_IMPL.refresh(accessToken, clientToken, requestUser);
    if (result.has("error")) {
      LoggerHelper.logError(result.toString(), false);
      return result;
    }

    LoggerHelper.logInfo(result.toString(), false);
    return result;
  }

  public static boolean isSessionExpired(String accessToken, String clientToken) {
    JSONObject session = YggdrasilHelper.validate(accessToken, clientToken);
    if (session.has("error")) {
      session = YggdrasilHelper.refresh(accessToken, clientToken, true);
      if (session.has("error")) {
        LoggerHelper.logError("Failed to refresh access token", true);
        return false;
      }

      String newAccessToken = session.getString("accessToken");
      String newClientToken = session.getString("clientToken");
      LauncherConfig.instance.setAccessToken(newAccessToken);
      LauncherConfig.instance.setClientToken(newClientToken);
      LauncherConfig.instance.save();
    }
    return true;
  }
}
