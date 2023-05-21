/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
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

  private static URL[] getYggdrasilAuthUrls() {
    URL[] authUrls = new URL[3];

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
      LOGGER.error("Cannot create URL for Mojang API", murle);
    }
    return authUrls;
  }

  public static JSONObject authenticate(String username, String password, String clientToken) {
    JSONObject agent = new JSONObject();
    agent.put("name", "Minecraft");
    agent.put("version", 1);

    JSONObject body = new JSONObject();
    body.put("agent", agent);
    body.put("username", username);
    body.put("password", password);
    body.put("clientToken", clientToken);
    body.put("requestUser", true);

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[0].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
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
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        LauncherUtils.swapContainers(
            LauncherPanel.instance,
            new LauncherNoNetworkPanel("lnnp.errorLabel.signin_null", cause.getMessage()));
        return null;
      }

      LOGGER.error("Error while authenticating with Mojang", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject validateAccessToken(String accessToken, String clientToken) {
    JSONObject body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[1].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
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
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        return null;
      }

      LOGGER.error("Error while validating access token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }

  public static JSONObject refreshAccessToken(String accessToken, String clientToken) {
    JSONObject body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);
    body.put("requestUser", true);

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future =
        service.submit(
            () ->
                Request.post(getYggdrasilAuthUrls()[2].toURI())
                    .bodyString(body.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          HttpEntity responseEntity = response.getEntity();
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
      Throwable cause = ee.getCause();
      if (cause instanceof UnknownHostException) {
        return null;
      }

      LOGGER.error("Error while refreshing access token", cause);
    } finally {
      service.shutdown();
    }
    return null;
  }
}
