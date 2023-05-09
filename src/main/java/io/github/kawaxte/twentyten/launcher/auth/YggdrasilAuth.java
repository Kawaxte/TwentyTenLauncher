package io.github.kawaxte.twentyten.launcher.auth;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public final class YggdrasilAuth {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(YggdrasilAuth.class);
  }

  private YggdrasilAuth() {}

  public static URL[] getYggdrasilAuthUrls() {
    val authUrls = new URL[3];

    try {
      authUrls[0] =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("authenticate")
                  .toString());
      authUrls[1] =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("validate")
                  .toString());
      authUrls[2] =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("refresh")
                  .toString());
    } catch (MalformedURLException murle) {
      LOGGER.error("Failed to create URL for Mojang API", murle);
    }
    return authUrls;
  }

  public static JSONObject authenticate(String username, String password, String clientToken) {
    val agent = new JSONObject();
    agent.put("name", "Minecraft");
    agent.put("version", 1);

    val body = new JSONObject();
    body.put("agent", agent);
    body.put("username", username);
    body.put("password", password);
    body.put("clientToken", clientToken);
    body.put("requestUser", true);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[0].toURI())
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

      LOGGER.error("Interrupted while authenticating with Mojang", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while authenticating with Mojang", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject validateAccessToken(String accessToken, String clientToken) {
    val body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
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

      LOGGER.error("Interrupted while validating access token", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while validating access token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject refreshAccessToken(String accessToken, String clientToken) {
    val body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);
    body.put("requestUser", true);

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

      LOGGER.error("Interrupted while refreshing access token", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while refreshing access token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return null;
  }
}
