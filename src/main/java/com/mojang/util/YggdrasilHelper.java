package com.mojang.util;

import com.mojang.YggdrasilAuthenticationImpl;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.util.LoggerHelper;
import org.json.JSONObject;

public final class YggdrasilHelper {

  public static final String MOJANG_SERVER_URL;
  private static final YggdrasilAuthenticationImpl YGGDRASIL_AUTHENTICATION;

  static {
    MOJANG_SERVER_URL = "https://authserver.mojang.com/%s";
    YGGDRASIL_AUTHENTICATION = new YggdrasilAuthenticationImpl();
  }

  public static JSONObject authenticate(final String username,
      final String password, final boolean requestUser,
      final LauncherLoginPanel loginPanel) {
    final JSONObject[] result = new JSONObject[1];

    Thread authenticationThread = new Thread(new Runnable() {
      public void run() {
        result[0] = YGGDRASIL_AUTHENTICATION.authenticate(username, password,
            requestUser);
        if (result[0].has("error")) {
          LoggerHelper.logError(result[0].toString(), false);
          return;
        }

        String accessToken = result[0].getString("accessToken");
        String newClientToken = result[0].getString("clientToken");
        if (!result[0].has("selectedProfile")) {
          LauncherConfig.instance.setProfileName("");
          LauncherConfig.instance.setProfileId("");
        } else {
          String name = result[0].getJSONObject("selectedProfile")
              .getString("name");
          String id = result[0].getJSONObject("selectedProfile")
              .getString("id");

          LauncherConfig.instance.setProfileName(name);
          LauncherConfig.instance.setProfileId(id);

          boolean isPasswordSaved = loginPanel.getRememberPasswordCheckBox()
              .isSelected();
          LauncherConfig.instance.setUsername(username);
          LauncherConfig.instance.setPassword(isPasswordSaved ? password : "");
          LauncherConfig.instance.setPasswordSaved(isPasswordSaved);
          LauncherConfig.instance.setAccessToken(accessToken);
          LauncherConfig.instance.setClientToken(newClientToken);
          LauncherConfig.instance.saveConfig();
        }
      }
    }, "yggdrasil");
    authenticationThread.start();
    try {
      authenticationThread.join();
    } catch (InterruptedException ie) {
      LoggerHelper.logError("Interrupted while waiting for authentication",
          true);
    }
    return result[0];
  }

  public static void validateAndRefresh(final String accessToken,
      final String clientToken, final boolean requestUser) {
    Thread validationThread = new Thread(new Runnable() {
      public void run() {
        JSONObject result = YGGDRASIL_AUTHENTICATION.validate(accessToken,
            clientToken);
        if (result.has("error")) {
          LoggerHelper.logError(result.toString(), false);
          Thread refreshThread = new Thread(new Runnable() {
            public void run() {
              JSONObject refreshResult = YGGDRASIL_AUTHENTICATION.refresh(
                  accessToken, clientToken, requestUser);
              if (refreshResult.has("error")) {
                LoggerHelper.logError(refreshResult.toString(), false);
                LauncherFrame.isSessionExpired = true;
                return;
              }

              String newAccessToken = refreshResult.getString("accessToken");
              String newClientToken = refreshResult.getString("clientToken");

              LauncherConfig.instance.setAccessToken(newAccessToken);
              LauncherConfig.instance.setClientToken(newClientToken);
              LauncherConfig.instance.saveConfig();

              LauncherFrame.isSessionExpired = false;
            }
          });
          refreshThread.start();
          try {
            refreshThread.join();
          } catch (InterruptedException ie) {
            LoggerHelper.logError("Interrupted while refreshing the session",
                ie, true);
          }
        }
      }
    }, "yggdrasil");
    validationThread.start();
  }
}
