package com.microsoft.util;

import com.microsoft.MicrosoftAuthenticationImpl;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.AuthenticationUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MinecraftUtils;
import org.json.JSONObject;

public final class MicrosoftUtils {

  public static URL msonlineUserCodeUrl;
  public static URL msonlineTokenUrl;
  public static URL xblAuthUrl;
  public static URL xstsAuthUrl;
  public static URL mcservicesLoginUrl;
  public static URL mcservicesStoreUrl;
  public static URL mcservicesProfileUrl;
  public static JSONObject pollingResult;
  @Getter
  @Setter
  private static MicrosoftAuthenticationImpl instance;

  static {
    MicrosoftUtils.setInstance(new MicrosoftAuthenticationImpl());

    try {
      MicrosoftUtils.msonlineUserCodeUrl = new URL(
          "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode");
      MicrosoftUtils.msonlineTokenUrl = new URL(
          "https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
      MicrosoftUtils.xblAuthUrl = new URL("https://user.auth.xboxlive.com/user/authenticate");
      MicrosoftUtils.xstsAuthUrl = new URL("https://xsts.auth.xboxlive.com/xsts/authorize");
      MicrosoftUtils.mcservicesLoginUrl = new URL(
          "https://api.minecraftservices.com/authentication/login_with_xbox");
      MicrosoftUtils.mcservicesStoreUrl = new URL(
          "https://api.minecraftservices.com/entitlements/mcstore");
      MicrosoftUtils.mcservicesProfileUrl = new URL(
          "https://api.minecraftservices.com/minecraft/profile");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private MicrosoftUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void getAndSetNewRefreshToken(final JSONObject result) {
    String newRefreshToken = result.getString("refresh_token");
    int newRefreshTokenExpiresIn = result.getInt("expires_in");
    long newRefreshTokenObtainTime =
        System.currentTimeMillis() + (newRefreshTokenExpiresIn * 1000L);
    Date newRefreshTokenObtainDate = new Date(newRefreshTokenObtainTime);
    ConfigUtils.getInstance().setMicrosoftRefreshToken(newRefreshToken);
    ConfigUtils.getInstance()
        .setMicrosoftRefreshTokenExpiresIn(newRefreshTokenObtainDate.getTime());
  }

  public static void getAndSetNewMinecraftToken(final JSONObject[] result) {
    String newMinecraftToken = result[0].getString("access_token");
    int newMinecraftTokenExpiresIn = result[0].getInt("expires_in");
    long newMinecraftTokenObtainTime =
        System.currentTimeMillis() + (newMinecraftTokenExpiresIn * 1000L);
    Date newMinecraftTokenObtainDate = new Date(newMinecraftTokenObtainTime);
    ConfigUtils.getInstance().setMicrosoftAccessToken(newMinecraftToken);
    ConfigUtils.getInstance()
        .setMicrosoftAccessTokenExpiresIn(newMinecraftTokenObtainDate.getTime());
  }

  public static void loginWithMicrosoft() {
    if (AuthenticationUtils.isMicrosoftSessionValid(
        ConfigUtils.getInstance().getMicrosoftAccessToken(),
        ConfigUtils.getInstance().getMicrosoftRefreshToken())) {
      if (!AuthenticationUtils.isMicrosoftProfileValid(
          ConfigUtils.getInstance().getMicrosoftAccessToken(),
          ConfigUtils.getInstance().getMicrosoftProfileName(),
          ConfigUtils.getInstance().getMicrosoftProfileId())) {
        MinecraftUtils.launchMinecraft();
        return;
      }
      MinecraftUtils.launchMinecraft(ConfigUtils.getInstance().getMicrosoftProfileName(),
          ConfigUtils.getInstance().getMicrosoftSessionId());
      return;
    }
    MicrosoftUtils.getInstance().login();
  }

  public static JSONObject refreshMinecraftToken(String clientId, String refreshToken) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", clientId);
    data.put("grant_type", "refresh_token");
    data.put("refresh_token", refreshToken);
    data.put("scope", "XboxLive.signin offline_access");
    return RequestUtils.performJsonRequest(MicrosoftUtils.msonlineTokenUrl, EMethod.POST,
        EHeader.X_WWW_FORM_URLENCODED.getHeader(), AuthenticationUtils.ofFormData(data));
  }

  public static JSONObject acquireXblToken(String accessToken) {
    JSONObject xblTokenResult = MicrosoftUtils.getInstance().acquireXblToken(accessToken);

    String xblToken = xblTokenResult.getString("Token");
    return MicrosoftUtils.acquireXstsToken(xblToken);
  }

  public static JSONObject acquireXstsToken(String xblToken) {
    JSONObject xstsTokenResult = MicrosoftUtils.getInstance().acquireXstsToken(xblToken);
    if (xstsTokenResult.has("XErr")) {
      MicrosoftUtils.getInstance().handleXstsTokenErrors(xstsTokenResult);
    }

    String uhs = xstsTokenResult.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0)
        .getString("uhs");
    String xstsToken = xstsTokenResult.getString("Token");
    return MicrosoftUtils.acquireMinecraftToken(uhs, xstsToken);
  }

  public static JSONObject acquireMinecraftToken(String uhs, String xstsToken) {
    return MicrosoftUtils.getInstance().acquireMinecraftToken(uhs, xstsToken);
  }

  public static JSONObject acquireMinecraftStore(String minecraftToken) {
    return MicrosoftUtils.getInstance().acquireMinecraftStore(minecraftToken);
  }

  public static JSONObject acquireMinecraftProfile(String minecraftToken) {
    return MicrosoftUtils.getInstance().acquireMinecraftProfile(minecraftToken);
  }
}
