package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public final class MicrosoftAuth {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(MicrosoftAuth.class);
  }

  private MicrosoftAuth() {}

  private static URL[] getYggdrasilAuthUrls() {
    val urls = new URL[7];
    try {
      urls[0] =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("devicecode")
                  .toString());
      urls[1] =
          new URL(
              new StringBuilder()
                  .append("https://login.microsoftonline.com/")
                  .append("consumers/")
                  .append("oauth2/")
                  .append("v2.0/")
                  .append("token")
                  .toString());
      urls[2] =
          new URL(
              new StringBuilder()
                  .append("https://user.auth.xboxlive.com/")
                  .append("user/")
                  .append("authenticate")
                  .toString());
      urls[3] =
          new URL(
              new StringBuilder()
                  .append("https://xsts.auth.xboxlive.com/")
                  .append("xsts/")
                  .append("authorize")
                  .toString());
      urls[4] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("authentication/")
                  .append("login_with_xbox")
                  .toString());
      urls[5] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("entitlements/")
                  .append("mcstore")
                  .toString());
      urls[6] =
          new URL(
              new StringBuilder()
                  .append("https://api.minecraftservices.com/")
                  .append("minecraft/")
                  .append("profile")
                  .toString());
    } catch (MalformedURLException murle) {
      LOGGER.error("Cannot create URL for Microsoft API", murle);
    }
    return urls;
  }

  public static JSONObject acquireDeviceCode(String clientId) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("response_type", "code");
    body.add("scope", "XboxLive.signin offline_access");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[0].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring device code", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring device code", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject acquireToken(String clientId, String deviceCode) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("device_code", deviceCode);
    body.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring token", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject refreshToken(String clientId, String refreshToken) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("refresh_token", refreshToken);
    body.add("grant_type", "refresh_token");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
                    .bodyForm(body.build())
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while refreshing token", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        return null;
      }

      LOGGER.error("Error while refreshing token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject acquireXBLToken(String accessToken) {
    val properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", String.format("d=%s", accessToken));

    val body = new JSONObject();
    body.put("Properties", properties);
    body.put("RelyingParty", "http://auth.xboxlive.com");
    body.put("TokenType", "JWT");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[2].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring XBL token", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring XBL token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject acquireXSTSToken(String token) {
    val properties = new JSONObject();
    properties.put("SandboxId", "RETAIL");
    properties.put("UserTokens", new String[] {token});

    val body = new JSONObject();
    body.put("Properties", properties);
    body.put("RelyingParty", "rp://api.minecraftservices.com/");
    body.put("TokenType", "JWT");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[3].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring XSTS token", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring XSTS token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject acquireAccessToken(String uhs, String token) {
    val body = new JSONObject();
    body.put("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));
    body.put("ensureLegacyEnabled", true);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[4].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring access token", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring access token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject checkEntitlementsMcStore(String accessToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.get(getYggdrasilAuthUrls()[5].toURI())
                    .addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while acquiring items", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while acquiring items", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject acquireMinecraftProfile(String accessToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.get(getYggdrasilAuthUrls()[6].toURI())
                    .addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return Objects.nonNull(responseEntity)
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();

      LOGGER.error("Interrupted while checking Minecraft profile", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while checking Minecraft profile", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
