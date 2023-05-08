package io.github.kawaxte.twentyten.launcher.auth;

import java.net.MalformedURLException;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

abstract class AbstractMicrosoftAuth {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(AbstractMicrosoftAuth.class);
  }

  @Getter private final String clientId;
  protected URL consumersDeviceCodeUrl;
  protected URL consumersTokenUrl;
  protected URL userAuthenticateUrl;
  protected URL xstsAuthorizeUrl;
  protected URL authenticationLoginWithXboxUrl;
  protected URL entitlementsMcStoreUrl;
  protected URL minecraftProfileUrl;
  @Getter @Setter private String accessToken;
  @Getter @Setter private String refreshToken;

  {
    try {
      consumersDeviceCodeUrl =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("devicecode")
                  .toString());
      consumersTokenUrl =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("token")
                  .toString());
      userAuthenticateUrl =
          new URL(
              new StringBuilder()
                  .append("https://user.auth.xboxlive.com/")
                  .append("user/")
                  .append("authenticate")
                  .toString());
      xstsAuthorizeUrl =
          new URL(
              new StringBuilder()
                  .append("https://xsts.auth.xboxlive.com/")
                  .append("xsts/")
                  .append("authorize")
                  .toString());
      authenticationLoginWithXboxUrl =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("authentication/")
                  .append("login_with_xbox")
                  .toString());
      entitlementsMcStoreUrl =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("entitlements/")
                  .append("mcstore")
                  .toString());
      minecraftProfileUrl =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("minecraft/")
                  .append("profile")
                  .toString());
    } catch (MalformedURLException murle) {
      LOGGER.error("Failed to create URL", murle);
    }

    this.clientId = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
  }

  public abstract JSONObject acquireDeviceCode(String clientId);

  public abstract JSONObject acquireToken(String clientId, String deviceCode);

  public abstract JSONObject refreshToken(String clientId, String refreshToken);

  public abstract JSONObject acquireXBLToken(String accessToken);

  public abstract JSONObject acquireXSTSToken(String token);

  public abstract JSONObject acquireAccessToken(String uhs, String token);

  public abstract JSONObject acquireMinecraftStoreItems(String accessToken);

  public abstract JSONObject acquireMinecraftProfile(String accessToken);
}
