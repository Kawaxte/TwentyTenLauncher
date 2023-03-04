package com.microsoft;

import com.microsoft.util.MicrosoftUtils;
import ee.twentyten.log.ELevel;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherMicrosoftLoginPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.AuthenticationUtils;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.RequestUtils;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import net.minecraft.util.MinecraftUtils;
import org.json.JSONObject;

public class MicrosoftAuthenticationImpl extends MicrosoftAuthentication {

  @Override
  public void authenticate() {
    final JSONObject authenticateResult = this.acquireUserCode(this.clientId);

    final String deviceCode = authenticateResult.getString("device_code");
    String userCode = authenticateResult.getString("user_code");
    String verificationUri = authenticateResult.getString("verification_uri");
    final int interval = authenticateResult.getInt("interval");
    final int expiresIn = authenticateResult.getInt("expires_in");
    if (userCode != null && verificationUri != null) {
      LauncherUtils.addPanel(LauncherPanel.getInstance(),
          new LauncherMicrosoftLoginPanel(userCode, verificationUri, expiresIn));
      final SwingWorker<JSONObject, Void> pollingWorker = new SwingWorker<JSONObject, Void>() {
        @Override
        protected JSONObject doInBackground() {
          return MicrosoftAuthenticationImpl.this.pollForAccessToken(deviceCode, expiresIn,
              interval);
        }

        @Override
        protected void done() {
          try {
            MicrosoftUtils.pollingResult = this.get();
          } catch (ExecutionException ee) {
            LoggerUtils.logMessage("Failed to authenticate with Microsoft", ee, ELevel.ERROR);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LoggerUtils.logMessage("Interrupted while authenticating with Microsoft", ie,
                ELevel.ERROR);
          } finally {
            if (MicrosoftUtils.pollingResult != null) {
              String minecraftToken = MicrosoftUtils.pollingResult.getString("access_token");
              int minecraftTokenExpiresIn = MicrosoftUtils.pollingResult.getInt("expires_in");
              long minecraftTokenObtainTime =
                  System.currentTimeMillis() + (minecraftTokenExpiresIn * 1000L);
              Date minecraftTokenObtainDate = new Date(minecraftTokenObtainTime);
              ConfigUtils.getInstance().setMicrosoftAccessToken(minecraftToken);
              ConfigUtils.getInstance()
                  .setMicrosoftAccessTokenExpiresIn(minecraftTokenObtainDate.getTime());

              MicrosoftUtils.pollingResult = MicrosoftUtils.acquireMinecraftStore(minecraftToken);
              if (MicrosoftUtils.pollingResult.has("items")) {
                MicrosoftUtils.pollingResult = MicrosoftUtils.acquireMinecraftProfile(
                    minecraftToken);

                String profileName = MicrosoftUtils.pollingResult.getString("name");
                String profileId = MicrosoftUtils.pollingResult.getString("id");
                ConfigUtils.getInstance().setMicrosoftProfileName(profileName);
                ConfigUtils.getInstance().setMicrosoftProfileId(profileId);
                ConfigUtils.getInstance().setMicrosoftSessionId(
                    ConfigUtils.formatSessionId(ConfigUtils.getInstance().getClientToken(),
                        ConfigUtils.getInstance().getMicrosoftAccessToken(),
                        ConfigUtils.getInstance().getMicrosoftProfileId()));
                ConfigUtils.writeToConfig();

                MinecraftUtils.launchMinecraft(ConfigUtils.getInstance().getMicrosoftProfileName(),
                    ConfigUtils.getInstance().getMicrosoftSessionId());
              } else {
                MinecraftUtils.launchMinecraft();
              }
            }
          }
        }
      };
      pollingWorker.execute();
    }
  }

  @Override
  public JSONObject pollForAccessToken(final String deviceCode, final int expiresIn,
      final int interval) {
    final ScheduledExecutorService pollingService = Executors.newSingleThreadScheduledExecutor();
    final AtomicBoolean isAuthorised = new AtomicBoolean(false);
    final AtomicBoolean isExpired = new AtomicBoolean(false);
    final Future<?>[] pollingTask = new Future<?>[1];
    pollingTask[0] = pollingService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        JSONObject accessTokenResult = MicrosoftAuthenticationImpl.this.acquireAccessToken(
            MicrosoftAuthenticationImpl.this.clientId, deviceCode);
        if (!LauncherMicrosoftLoginPanel.getInstance().isShowing()) {
          pollingTask[0].cancel(true);
          return;
        }
        if (accessTokenResult.has("error")) {
          MicrosoftAuthenticationImpl.this.handleDeviceCodeErrors(accessTokenResult, isExpired,
              pollingTask);
        } else {
          isAuthorised.set(true);
          MicrosoftAuthenticationImpl.this.accessToken = accessTokenResult.getString(
              "access_token");
          String refreshToken = accessTokenResult.getString("refresh_token");
          int refreshTokenExpiresIn = accessTokenResult.getInt("expires_in");
          long refreshTokenObtainTime =
              System.currentTimeMillis() + (refreshTokenExpiresIn * 1000L);
          Date refreshTokenObtainDate = new Date(refreshTokenObtainTime);
          ConfigUtils.getInstance().setMicrosoftRefreshToken(refreshToken);
          ConfigUtils.getInstance()
              .setMicrosoftRefreshTokenExpiresIn(refreshTokenObtainDate.getTime());

          LauncherMicrosoftLoginPanel.getInstance().getCopyUserCodeLabel().setVisible(false);
          LauncherMicrosoftLoginPanel.getInstance().getUserCodeLabel().setVisible(false);
          LauncherMicrosoftLoginPanel.getInstance().getExpiresInProgressBar()
              .setIndeterminate(true);
          LauncherMicrosoftLoginPanel.getInstance().getOpenBrowserButton().setVisible(false);
          pollingTask[0].cancel(true);
        }
        MicrosoftAuthenticationImpl.this.startProgressBar(
            LauncherMicrosoftLoginPanel.getInstance().getExpiresInProgressBar(),
            isAuthorised.get());
      }
    }, 0, interval * 200L, TimeUnit.MILLISECONDS);
    try {
      pollingTask[0].get(expiresIn, TimeUnit.SECONDS);
    } catch (ExecutionException ee) {
      LoggerUtils.logMessage("Failed to poll for access token", ee, ELevel.ERROR);
    } catch (CancellationException ce) {
      if (!isAuthorised.get() && !isExpired.get()) {
        LoggerUtils.logMessage("Cancellation while polling for access token", ce, ELevel.ERROR);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      LoggerUtils.logMessage("Interrupted while polling for access token", ie, ELevel.ERROR);
    } catch (TimeoutException te) {
      isExpired.set(true);
      LoggerUtils.logMessage("Timeout while polling for access token", te, ELevel.ERROR);
    } finally {
      pollingService.shutdownNow();
    }
    return isAuthorised.get() && !isExpired.get() ? MicrosoftUtils.acquireXblToken(this.accessToken)
        : null;
  }

  @Override
  public JSONObject acquireUserCode(String clientId) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", clientId);
    data.put("scope", "XboxLive.signin offline_access");
    return RequestUtils.performJsonRequest(MicrosoftUtils.msonlineUserCodeUrl, EMethod.POST,
        EHeader.X_WWW_FORM_URLENCODED.getHeader(), AuthenticationUtils.ofFormData(data));
  }

  @Override
  public JSONObject acquireAccessToken(String clientId, String deviceCode) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", clientId);
    data.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
    data.put("device_code", deviceCode);
    return RequestUtils.performJsonRequest(MicrosoftUtils.msonlineTokenUrl, EMethod.POST,
        EHeader.X_WWW_FORM_URLENCODED.getHeader(), AuthenticationUtils.ofFormData(data));
  }

  @Override
  public JSONObject acquireXblToken(String accessToken) {
    JSONObject properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", "d=" + accessToken);

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "http://auth.xboxlive.com");
    data.put("TokenType", "JWT");
    return RequestUtils.performJsonRequest(MicrosoftUtils.xblAuthUrl, EMethod.POST,
        EHeader.JSON.getHeader(), data);
  }

  @Override
  public JSONObject acquireXstsToken(String xblToken) {
    JSONObject properties = new JSONObject();
    properties.put("SandboxId", "RETAIL");
    properties.put("UserTokens", new String[]{xblToken});

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "rp://api.minecraftservices.com/");
    data.put("TokenType", "JWT");
    return RequestUtils.performJsonRequest(MicrosoftUtils.xstsAuthUrl, EMethod.POST,
        EHeader.JSON.getHeader(), data);
  }

  @Override
  public JSONObject acquireMinecraftToken(String uhs, String xstsToken) {
    JSONObject data = new JSONObject();
    data.put("identityToken", MessageFormat.format("XBL3.0 x={0};{1}", uhs, xstsToken));
    return RequestUtils.performJsonRequest(MicrosoftUtils.mcservicesLoginUrl, EMethod.POST,
        EHeader.JSON.getHeader(), data);
  }

  @Override
  public JSONObject acquireMinecraftStore(String minecraftToken) {
    Map<String, String> header = new HashMap<>();
    header.put("Authorization", MessageFormat.format("Bearer {0}", minecraftToken));
    header.putAll(EHeader.JSON.getHeader());
    return RequestUtils.performJsonRequest(MicrosoftUtils.mcservicesStoreUrl, EMethod.GET, header);
  }

  @Override
  public JSONObject acquireMinecraftProfile(String minecraftToken) {
    Map<String, String> header = new HashMap<>();
    header.put("Authorization", MessageFormat.format("Bearer {0}", minecraftToken));
    header.putAll(EHeader.JSON.getHeader());
    return RequestUtils.performJsonRequest(MicrosoftUtils.mcservicesProfileUrl, EMethod.GET,
        header);
  }
}
