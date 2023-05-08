package io.github.kawaxte.twentyten.launcher.auth;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

public class AbstractYggdrasilAuthImpl extends AbstractYggdrasilAuth {

  @Override
  public JSONObject authenticate(String username, String password, String clientToken) {
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
                Request.post(authenticateUrl.toURI())
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

      this.logger.error("Interrupted while authenticating with Mojang", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while authenticating with Mojang", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }

  @Override
  public JSONObject validateAccessToken(String accessToken, String clientToken) {
    val body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(validateUrl.toURI())
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

      this.logger.error("Interrupted while validating access token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while validating access token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }

  @Override
  public JSONObject refreshAccessToken(String accessToken, String clientToken) {
    val body = new JSONObject();
    body.put("accessToken", accessToken);
    body.put("clientToken", clientToken);
    body.put("requestUser", true);

    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(refreshUrl.toURI())
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

      this.logger.error("Interrupted while refreshing access token", ie);
    } catch (ExecutionException ee) {
      this.logger.error("Error while refreshing access token", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }
}
