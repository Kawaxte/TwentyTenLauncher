package com.microsoft.util;

import com.microsoft.MicrosoftAuthenticationImpl;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.LoggerHelper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.json.JSONObject;

public final class MicrosoftHelper {

  public static final MicrosoftAuthenticationImpl MICROSOFT_AUTHENTICATION;
  public static final String MSONLINE_DEVICE_CODE_URL;
  public static final String MSONLINE_TOKEN_URL;
  public static final String XBL_AUTH_URL;
  public static final String XSTS_AUTH_URL;
  public static final String MCSERVICES_LOGIN_URL;
  public static final String MCSERVICES_STORE_URL;
  public static final String MCSERVICES_PROFILE_URL;

  static {
    MICROSOFT_AUTHENTICATION = new MicrosoftAuthenticationImpl();

    MSONLINE_DEVICE_CODE_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";
    MSONLINE_TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";

    XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";

    MCSERVICES_LOGIN_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    MCSERVICES_STORE_URL = "https://api.minecraftservices.com/entitlements/mcstore";
    MCSERVICES_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
  }

  private MicrosoftHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  private static void handleXstsError(JSONObject result) {
    int XerrCode = result.getInt("XErr");
    switch (XerrCode) {
      case (int) 2148916233L:
        LoggerHelper.logError(
            "Following account doesn't have an Xbox Live account", true);
        break;
      case (int) 2148916235L:
        LoggerHelper.logError(
            "Following account is from a country that doesn't support Xbox Live",
            true);
        break;
      case (int) 2148916236L:
        LoggerHelper.logError(
            "Following account must complete the Xbox Live parental consent process",
            true);
        break;
      case (int) 2148916237L:
        LoggerHelper.logError(
            "Following account must complete the Xbox Live age verification process",
            true);
        break;
      case (int) 2148916238L:
        LoggerHelper.logError(
            "Following account must be added to the Xbox Live family account",
            true);
        break;
      default:
        LoggerHelper.logError(String.valueOf(XerrCode), true);
        break;
    }
  }

  public static void authenticate(final LauncherPanel panel) {
    final String[] userCode = new String[1];
    final int[] expiresIn = new int[1];
    final String[] verificationUri = new String[1];

    final JSONObject[] userCodeResult = new JSONObject[1];
    Thread authenticateThread = new Thread(new Runnable() {
      @Override
      public void run() {
        userCodeResult[0] = MICROSOFT_AUTHENTICATION.acquireUserCode(panel);

        userCode[0] = userCodeResult[0].getString("user_code");
        expiresIn[0] = userCodeResult[0].getInt("expires_in");
        verificationUri[0] = userCodeResult[0].getString("verification_uri");

        final String deviceCode = userCodeResult[0].getString("device_code");
        final int interval = userCodeResult[0].getInt("interval");

        final Timer pollTimer = new Timer(interval * 200, null);
        pollTimer.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();

            MicrosoftHelper.poll(panel, deviceCode, pollTimer, panel);
            if (src instanceof Timer) {
              ((Timer) src).start();
            }
          }
        });
        pollTimer.setRepeats(false);
        pollTimer.start();
      }
    }, "microsoft");
    authenticateThread.start();
    try {
      authenticateThread.join();
    } catch (InterruptedException ie) {
      LoggerHelper.logError("Interrupted while waiting for authentication", ie,
          true);
    }

    panel.addMicrosoftLoginPanel(userCode[0], expiresIn[0], verificationUri[0]);
  }

  public static JSONObject acquire(final LauncherPanel panel,
      String accessToken) {
    JSONObject xblTokenResult = MICROSOFT_AUTHENTICATION.acquireXblToken(
        accessToken);
    Objects.requireNonNull(xblTokenResult, "xblTokenResult == null!");

    String xblToken = xblTokenResult.getString("Token");

    JSONObject xstsTokenResult = MICROSOFT_AUTHENTICATION.acquireXstsToken(
        xblToken);
    Objects.requireNonNull(xstsTokenResult, "xstsTokenResult == null!");
    if (xstsTokenResult.has("XErr")) {
      MicrosoftHelper.handleXstsError(xstsTokenResult);

      panel.addNoNetworkPanel(panel.getLoginPanel().getErrorLabelFailedText());
    }

    String uhs = xstsTokenResult.getJSONObject("DisplayClaims")
        .getJSONArray("xui").getJSONObject(0).getString("uhs");
    String xstsToken = xstsTokenResult.getString("Token");
    return MICROSOFT_AUTHENTICATION.acquireMinecraftToken(uhs, xstsToken);
  }

  public static void refresh(LauncherPanel panel, String refreshToken) {
    JSONObject refreshResult = MICROSOFT_AUTHENTICATION.refreshAccessToken(
        refreshToken);
    Objects.requireNonNull(refreshResult, "refreshResult == null!");

    String accessToken = refreshResult.getString("access_token");

    JSONObject minecraftTokenResult = MicrosoftHelper.acquire(panel,
        accessToken);
    Objects.requireNonNull(minecraftTokenResult,
        "minecraftTokenResult == null!");

    String minecraftToken = minecraftTokenResult.getString("access_token");

    LauncherConfig.instance.setAccessToken(minecraftToken);
  }

  public static String ofFormData(Map<Object, Object> data) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Object, Object> entry : data.entrySet()) {
      sb.append(entry.getKey()).append("=").append(entry.getValue())
          .append("&");
    }
    return sb.toString();
  }

  public static void poll(final LauncherPanel panel, final String deviceCode,
      final Timer timer, final ActionListener callback) {
    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() {
        JSONObject pollResult = MICROSOFT_AUTHENTICATION.acquireAccessToken(
            deviceCode);
        Objects.requireNonNull(pollResult, "pollResult == null!");

        boolean hasError = pollResult.has("error");
        if (hasError) {
          LoggerHelper.logError(String.valueOf(pollResult), false);

          String error = pollResult.getString("error");
          switch (error) {
            case "authorization_pending":
              return true;
            case "expired_token":
              panel.addNoNetworkPanel(
                  panel.getLoginPanel().getErrorLabelFailedText());
              timer.stop();
              return false;
            default:
              timer.stop();
              return false;
          }
        }
        timer.stop();

        LoggerHelper.logInfo(pollResult.toString(), false);

        boolean hasTokens =
            pollResult.has("access_token") && pollResult.has("refresh_token");
        if (hasTokens) {
          String accessToken = pollResult.getString("access_token");
          String refreshToken = pollResult.getString("refresh_token");

          LauncherConfig.instance.setRefreshToken(refreshToken);

          JSONObject minecraftTokenResult = MicrosoftHelper.acquire(panel,
              accessToken);
          Objects.requireNonNull(minecraftTokenResult,
              "minecraftTokenResult == null!");

          String minecraftToken = minecraftTokenResult.getString(
              "access_token");

          LauncherConfig.instance.setAccessToken(minecraftToken);

          JSONObject minecraftStoreResult = MICROSOFT_AUTHENTICATION.acquireMinecraftStore(
              minecraftToken);
          Objects.requireNonNull(minecraftStoreResult,
              "minecraftStoreResult == null!");

          boolean hasPaid =
              minecraftStoreResult.getJSONArray("items").length() > 0;
          if (hasPaid) {
            JSONObject minecraftProfileResult = MICROSOFT_AUTHENTICATION.acquireMinecraftProfile(
                minecraftToken);
            Objects.requireNonNull(minecraftProfileResult,
                "minecraftProfileResult == null!");

            String profileId = minecraftProfileResult.getString("id");
            String profileName = minecraftProfileResult.getString("name");

            LauncherConfig.instance.setProfileId(profileId);
            LauncherConfig.instance.setProfileName(profileName);
            LauncherConfig.instance.saveConfig();

            String sessionId = LauncherConfig.instance.getSessionId();
            LauncherFrame.instance.launchMinecraft(profileName, sessionId,
                true);
            return false;
          }

          panel.addNoNetworkPanel(
              panel.getLoginPanel().getErrorLabelFailedText());
          return false;
        }
        return pollResult.has("access_token") && pollResult.has(
            "refresh_token");
      }

      @Override
      protected void done() {
        boolean isPolling = false;
        try {
          isPolling = get();
        } catch (InterruptedException ie) {
          LoggerHelper.logError("Interrupted while polling for authorisation",
              ie, true);
          timer.stop();
        } catch (ExecutionException ee) {
          LoggerHelper.logError("Exception while polling for authorisation", ee,
              true);
          timer.stop();
        }
        callback.actionPerformed(
            new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                Boolean.toString(isPolling)));
      }
    };
    worker.execute();
  }
}
