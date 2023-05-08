package io.github.kawaxte.twentyten.launcher.auth;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

public class AbstractMicrosoftAuthImpl extends AbstractMicrosoftAuth {

  @Override
  public JSONObject acquireDeviceCode(String clientId) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("response_type", "code");
    body.add("scope", "XboxLive.signin offline_access");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(consumersDeviceCodeUrl.toURI())
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

      this.logger.error("Interrupted while acquiring device code", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring device code", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireToken(String clientId, String deviceCode) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("device_code", deviceCode);
    body.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(consumersTokenUrl.toURI())
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

      this.logger.error("Interrupted while acquiring token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject refreshToken(String clientId, String refreshToken) {
    val body = Form.form();
    body.add("client_id", clientId);
    body.add("refresh_token", refreshToken);
    body.add("grant_type", "refresh_token");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(consumersTokenUrl.toURI())
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

      this.logger.error("Interrupted while refreshing token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while refreshing token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireXBLToken(String accessToken) {
    val properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", MessageFormat.format("d={0}", accessToken));

    val body = new JSONObject();
    body.put("Properties", properties);
    body.put("RelyingParty", "http://auth.xboxlive.com");
    body.put("TokenType", "JWT");

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(userAuthenticateUrl.toURI())
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

      this.logger.error("Interrupted while acquiring XBL token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring XBL token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireXSTSToken(String token) {
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
                Request.post(xstsAuthorizeUrl.toURI())
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

      this.logger.error("Interrupted while acquiring XSTS token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring XSTS token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireAccessToken(String uhs, String token) {
    val body = new JSONObject();
    body.put("identityToken", String.format("XBL3.0 x=%s;%s", uhs, token));
    body.put("ensureLegacyEnabled", true);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(authenticationLoginWithXboxUrl.toURI())
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

      this.logger.error("Interrupted while acquiring access token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring access token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireMinecraftStoreItems(String accessToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.get(entitlementsMcStoreUrl.toURI())
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

      this.logger.error("Interrupted while acquiring items", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while acquiring items", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  @Override
  public JSONObject acquireMinecraftProfile(String accessToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.get(minecraftProfileUrl.toURI())
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

      this.logger.error("Interrupted while checking Minecraft profile", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while checking Minecraft profile", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }
}
