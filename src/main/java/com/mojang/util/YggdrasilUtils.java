package com.mojang.util;

import com.mojang.YggdrasilAuthenticationImpl;
import ee.twentyten.log.ELevel;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.util.AuthenticationUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LoggerUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MinecraftUtils;
import org.json.JSONObject;

public final class YggdrasilUtils {

  public static URL authserverAuthenticateUrl;
  public static URL authserverRefreshUrl;
  public static URL authserverValidateUrl;
  public static boolean isAccessTokenExpired;
  @Getter
  @Setter
  private static YggdrasilAuthenticationImpl instance;

  static {
    YggdrasilUtils.setInstance(new YggdrasilAuthenticationImpl());

    try {
      YggdrasilUtils.authserverAuthenticateUrl = new URL(
          "https://authserver.mojang.com/authenticate");
      YggdrasilUtils.authserverRefreshUrl = new URL("https://authserver.mojang.com/refresh");
      YggdrasilUtils.authserverValidateUrl = new URL("https://authserver.mojang.com/validate");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private YggdrasilUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void loginWithYggdrasil() {
    boolean isUsernameValid = Objects.equals(
        LauncherLoginPanel.getInstance().getUsernameField().getText(),
        ConfigUtils.getInstance().getYggdrasilUsername());
    boolean isPasswordValid = Objects.equals(
        new String(LauncherLoginPanel.getInstance().getPasswordField().getPassword()),
        ConfigUtils.getInstance().getYggdrasilPassword());
    boolean isPasswordSavedValid = Objects.equals(
        LauncherLoginPanel.getInstance().getRememberPasswordCheckBox().isSelected(),
        ConfigUtils.getInstance().isYggdrasilPasswordSaved());
    if (isUsernameValid && isPasswordValid && isPasswordSavedValid) {
      if (AuthenticationUtils.isYggdrasilSessionValid(
          ConfigUtils.getInstance().getYggdrasilAccessToken())) {
        if (!AuthenticationUtils.isYggdrasilProfileValid(
            ConfigUtils.getInstance().getYggdrasilProfileId())) {
          MinecraftUtils.launchMinecraft();
          return;
        }
        MinecraftUtils.launchMinecraft(ConfigUtils.getInstance().getYggdrasilProfileName(),
            ConfigUtils.getInstance().getYggdrasilSessionId());
      }
      return;
    }
    YggdrasilUtils.getInstance().login();
  }

  public static JSONObject validate(String accessToken, String clientToken) {
    return YggdrasilUtils.getInstance().validate(accessToken, clientToken);
  }

  public static JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    return YggdrasilUtils.getInstance().refresh(accessToken, clientToken, requestUser);
  }
}
