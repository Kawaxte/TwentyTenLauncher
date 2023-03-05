package ee.twentyten.util;

import com.microsoft.util.MicrosoftUtils;
import com.mojang.util.YggdrasilUtils;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import org.json.JSONObject;

public final class AuthenticationUtils {

  private static final Pattern JWT_PATTERN = Pattern.compile(
      "^([a-zA-Z0-9_-]+\\.){2}[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*$");
  public static URL sessionserverProfileUrl;
  public static URL mcservicesProfileUrl;

  static {
    try {
      AuthenticationUtils.sessionserverProfileUrl = new URL(
          "https://sessionserver.mojang.com/session/minecraft/profile");
      AuthenticationUtils.mcservicesProfileUrl = new URL(
          "https://api.minecraftservices.com/minecraft/profile");
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
  }

  private AuthenticationUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static JSONObject checkYggdrasilProfile(String profileId) {
    try {
      AuthenticationUtils.sessionserverProfileUrl = new URL(
          AuthenticationUtils.sessionserverProfileUrl.toString() + "/" + profileId);
    } catch (MalformedURLException murle) {
      LoggerUtils.logMessage("Failed to create URL", murle, ELevel.ERROR);
    }
    return RequestUtils.performJsonRequest(
        AuthenticationUtils.sessionserverProfileUrl, EMethod.GET, EHeader.JSON.getHeader());
  }

  public static JSONObject checkMicrosoftProfile(String accessToken) {
    Map<String, String> header = new HashMap<>();
    header.put("Authorization", MessageFormat.format("Bearer {0}", accessToken));
    header.putAll(EHeader.JSON.getHeader());
    return RequestUtils.performJsonRequest(AuthenticationUtils.mcservicesProfileUrl, EMethod.GET,
        header);
  }

  public static boolean isYggdrasilSessionValid(String accessToken) {
    if (accessToken == null) {
      return false;
    }
    return JWT_PATTERN.matcher(accessToken).matches();
  }

  public static boolean isMicrosoftSessionValid(String accessToken, String refreshToken) {
    if (accessToken == null || refreshToken == null) {
      return false;
    }
    return JWT_PATTERN.matcher(accessToken).matches() && refreshToken.startsWith("M.R3_BL2.");
  }

  public static boolean isYggdrasilProfileValid(String profileId) {
    if (profileId == null) {
      return false;
    }

    JSONObject profileResult = AuthenticationUtils.checkYggdrasilProfile(profileId);

    String profileNameFromApi = profileResult.getString("name");
    String profileIdFromApi = profileResult.getString("id");
    return ConfigUtils.getInstance().getYggdrasilProfileName().equals(profileNameFromApi)
        && profileId.equals(profileIdFromApi);
  }

  public static boolean isMicrosoftProfileValid(String accessToken, String profileName,
      String profileId) {
    if (accessToken == null || profileName == null || profileId == null) {
      return false;
    }

    JSONObject profileResult = AuthenticationUtils.checkMicrosoftProfile(accessToken);

    String profileNameFromApi = profileResult.getString("name");
    String profileIdFromApi = profileResult.getString("id");
    return profileName.equals(profileNameFromApi) && profileId.equals(profileIdFromApi);
  }

  public static String ofFormData(Map<Object, Object> data) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      if (sb.length() > 0) {
        sb.append('&');
      }
      sb.append(entry.getKey());
      sb.append('=');
      sb.append(entry.getValue());
    }
    return sb.toString();
  }

  public static void checkMinecraftTokenExpirationTime(String refreshToken,
      long accessTokenExpiresIn) {
    long currentTime = System.currentTimeMillis();
    long timeRemaining = accessTokenExpiresIn - currentTime;
    long timeRemainingInSeconds = timeRemaining / 1000L;
    if (timeRemainingInSeconds <= 60) {
      MicrosoftUtils.isMinecraftTokenExpired = true;
      AuthenticationUtils.refreshMinecraftToken(refreshToken);
    }

    String expirationTimeString = AuthenticationUtils.formatExpirationTime(timeRemainingInSeconds);
    LoggerUtils.logMessage(
        MessageFormat.format("{0} ({1,number,#})", expirationTimeString, timeRemainingInSeconds),
        ELevel.INFO);
  }

  public static void validateAndRefreshAccessToken(final String clientToken) {
    SwingWorker<JSONObject, Void> validateAndRefreshWorker = new SwingWorker<JSONObject, Void>() {
      @Override
      protected JSONObject doInBackground() {
        JSONObject validateAccessTokenResult = YggdrasilUtils.validate(
            ConfigUtils.getInstance().getYggdrasilAccessToken(), clientToken);
        if (validateAccessTokenResult.has("error")) {
          LoggerUtils.logMessage("Failed to validate access token", ELevel.ERROR);
          validateAccessTokenResult = YggdrasilUtils.refresh(
              ConfigUtils.getInstance().getYggdrasilAccessToken(), clientToken, true);
          if (validateAccessTokenResult.has("error")) {
            YggdrasilUtils.isAccessTokenExpired = true;
            LoggerUtils.logMessage("Failed to refresh access token", ELevel.ERROR);
            return null;
          }
        }
        return validateAccessTokenResult;
      }

      @Override
      protected void done() {
        try {
          JSONObject validateAccessTokenResult = this.get();
          if (validateAccessTokenResult != null && !validateAccessTokenResult.isEmpty()) {
            String newAccessToken = validateAccessTokenResult.getString("accessToken");
            String newClientToken = validateAccessTokenResult.getString("clientToken");
            ConfigUtils.getInstance().setYggdrasilAccessToken(newAccessToken);
            ConfigUtils.getInstance().setClientToken(newClientToken);
            ConfigUtils.writeToConfig();
          }
        } catch (ExecutionException ee) {
          LoggerUtils.logMessage("Failed to validate and refresh access token", ee, ELevel.ERROR);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          LoggerUtils.logMessage("Interrupted while validating and refreshing access token", ie,
              ELevel.ERROR);
        }
      }
    };
    validateAndRefreshWorker.execute();
  }

  private static void refreshMinecraftToken(String refreshToken) {
    JSONObject refreshResult = MicrosoftUtils.refreshMinecraftToken(
        MicrosoftUtils.getInstance().getClientId(), refreshToken);

    String newRefreshToken = refreshResult.getString("refresh_token");
    int newRefreshTokenExpiresIn = refreshResult.getInt("expires_in");
    long newRefreshTokenObtainTime =
        System.currentTimeMillis() + (newRefreshTokenExpiresIn * 1000L);
    Date newRefreshTokenObtainDate = new Date(newRefreshTokenObtainTime);
    ConfigUtils.getInstance().setMicrosoftRefreshToken(newRefreshToken);
    ConfigUtils.getInstance()
        .setMicrosoftRefreshTokenExpiresIn(newRefreshTokenObtainDate.getTime());

    String newAccessToken = refreshResult.getString("access_token");
    MicrosoftUtils.acquireXblToken(newAccessToken);
  }

  private static String formatExpirationTime(long expirationTime) {
    long hours = expirationTime / 3600;
    long minutes = (expirationTime % 3600) / 60;
    long seconds = expirationTime % 60;
    long timezoneOffset =
        (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000 / 60
            / 60;
    String timeZoneString = MessageFormat.format("{0}{1,number,00}:00",
        timezoneOffset < 0 ? "-" : "+", Math.abs(timezoneOffset));
    return MessageFormat.format("{0,number,00}:{1,number,00}:{2,number,00}{3}", hours, minutes,
        seconds, timeZoneString);
  }
}
