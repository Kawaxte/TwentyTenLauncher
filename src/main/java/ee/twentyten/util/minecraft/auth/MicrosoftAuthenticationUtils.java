package ee.twentyten.util.minecraft.auth;

import ee.twentyten.log.ELevel;
import ee.twentyten.minecraft.auth.MicrosoftAuthenticationImpl;
import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.config.ConfigUtils;
import ee.twentyten.util.log.LoggerUtils;
import ee.twentyten.util.minecraft.MinecraftUtils;
import ee.twentyten.util.request.ConnectionRequestUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public final class MicrosoftAuthenticationUtils {

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
    MicrosoftAuthenticationUtils.setInstance(new MicrosoftAuthenticationImpl());

    try {
      MicrosoftAuthenticationUtils.msonlineUserCodeUrl = new URL(
          "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode");
      MicrosoftAuthenticationUtils.msonlineTokenUrl = new URL(
          "https://login.microsoftonline.com/consumers/oauth2/v2.0/token");
      MicrosoftAuthenticationUtils.xblAuthUrl = new URL(
          "https://user.auth.xboxlive.com/user/authenticate");
      MicrosoftAuthenticationUtils.xstsAuthUrl = new URL(
          "https://xsts.auth.xboxlive.com/xsts/authorize");
      MicrosoftAuthenticationUtils.mcservicesLoginUrl = new URL(
          "https://api.minecraftservices.com/authentication/login_with_xbox");
      MicrosoftAuthenticationUtils.mcservicesStoreUrl = new URL(
          "https://api.minecraftservices.com/entitlements/mcstore");
      MicrosoftAuthenticationUtils.mcservicesProfileUrl = new URL(
          "https://api.minecraftservices.com/minecraft/profile");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private MicrosoftAuthenticationUtils() {
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
    MicrosoftAuthenticationUtils.getInstance().login();
  }

  public static JSONObject refreshMinecraftToken(String clientId, String refreshToken) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", clientId);
    data.put("grant_type", "refresh_token");
    data.put("refresh_token", refreshToken);
    data.put("scope", "XboxLive.signin offline_access");

    return new ConnectionRequest.Builder()
        .setUrl(MicrosoftAuthenticationUtils.msonlineTokenUrl)
        .setMethod(EMethod.POST)
        .setHeaders(ConnectionRequestUtils.X_WWW_FORM_URLENCODED)
        .setBody(AuthenticationUtils.ofFormData(data))
        .setSSLSocketFactory(ConnectionRequestUtils.getSSLSocketFactory())
        .build().performJsonRequest();
  }

  public static JSONObject acquireXblToken(String accessToken) {
    JSONObject xblTokenResult = MicrosoftAuthenticationUtils.getInstance()
        .acquireXblToken(accessToken);

    String xblToken = xblTokenResult.getString("Token");
    return MicrosoftAuthenticationUtils.acquireXstsToken(xblToken);
  }

  public static JSONObject acquireXstsToken(String xblToken) {
    JSONObject xstsTokenResult = MicrosoftAuthenticationUtils.getInstance()
        .acquireXstsToken(xblToken);
    if (xstsTokenResult.has("XErr")) {
      MicrosoftAuthenticationUtils.getInstance().handleXstsTokenErrors(xstsTokenResult);
    }

    String uhs = xstsTokenResult.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0)
        .getString("uhs");
    String xstsToken = xstsTokenResult.getString("Token");
    return MicrosoftAuthenticationUtils.acquireMinecraftToken(uhs, xstsToken);
  }

  public static JSONObject acquireMinecraftToken(String uhs, String xstsToken) {
    return MicrosoftAuthenticationUtils.getInstance().acquireMinecraftToken(uhs, xstsToken);
  }

  public static JSONObject acquireMinecraftStore(String minecraftToken) {
    return MicrosoftAuthenticationUtils.getInstance().acquireMinecraftStore(minecraftToken);
  }

  public static JSONObject acquireMinecraftProfile(String minecraftToken) {
    return MicrosoftAuthenticationUtils.getInstance().acquireMinecraftProfile(minecraftToken);
  }
}
