package ee.twentyten.util;

import ee.twentyten.log.ELevel;
import ee.twentyten.minecraft.auth.YggdrasilAuthenticationImpl;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

public final class YggdrasilAuthenticationUtils {

  public static URL authserverAuthenticateUrl;
  public static URL authserverRefreshUrl;
  public static URL authserverValidateUrl;
  public static boolean isAccessTokenExpired;
  @Getter
  @Setter
  private static YggdrasilAuthenticationImpl instance;

  static {
    YggdrasilAuthenticationUtils.setInstance(new YggdrasilAuthenticationImpl());

    try {
      YggdrasilAuthenticationUtils.authserverAuthenticateUrl = new URL(
          "https://authserver.mojang.com/authenticate");
      YggdrasilAuthenticationUtils.authserverRefreshUrl = new URL(
          "https://authserver.mojang.com/refresh");
      YggdrasilAuthenticationUtils.authserverValidateUrl = new URL(
          "https://authserver.mojang.com/validate");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private YggdrasilAuthenticationUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void loginWithYggdrasil() {
    String username = LauncherLoginPanel.getInstance().getUsernameField().getText();
    String password = new String(LauncherLoginPanel.getInstance().getPasswordField().getPassword());
    boolean isPasswordSaved = LauncherLoginPanel.getInstance().getRememberPasswordCheckBox()
        .isSelected();

    boolean isUsernameValid =
        Objects.equals(username, ConfigUtils.getInstance().getYggdrasilUsername())
            && !username.isEmpty();
    boolean isPasswordValid =
        Objects.equals(password, ConfigUtils.getInstance().getYggdrasilPassword())
            && !password.isEmpty();
    if (isUsernameValid && isPasswordValid) {
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
    YggdrasilAuthenticationUtils.getInstance().login(username, password, isPasswordSaved);
  }

  public static JSONObject validate(String accessToken, String clientToken) {
    return YggdrasilAuthenticationUtils.getInstance().validate(accessToken, clientToken);
  }

  public static JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    return YggdrasilAuthenticationUtils.getInstance()
        .refresh(accessToken, clientToken, requestUser);
  }
}
