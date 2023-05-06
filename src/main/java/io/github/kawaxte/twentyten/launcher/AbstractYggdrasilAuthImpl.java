package io.github.kawaxte.twentyten.launcher;

import com.sun.istack.internal.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

public class AbstractYggdrasilAuthImpl extends AbstractYggdrasilAuth {

  @Override
  public JSONObject authenticate(
      @NotNull String username, @NotNull String password, @NotNull String clientToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(authenticateUrl.toURI())
                    .bodyString(
                        new JSONObject()
                            .put("agent", getAgent())
                            .put("username", username)
                            .put("password", password)
                            .put("clientToken", clientToken)
                            .put("requestUser", true)
                            .toString(),
                        ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return responseEntity != null
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      LOGGER.error("Interrupted while authenticating with Mojang", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while authenticating with Mojang", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }

  @Override
  public JSONObject validate(@NotNull String accessToken, @NotNull String clientToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(validateUrl.toURI())
                    .bodyString(
                        new JSONObject()
                            .put("accessToken", accessToken)
                            .put("clientToken", clientToken)
                            .toString(),
                        ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return responseEntity != null
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      LOGGER.error("Interrupted while validating with Mojang", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while validating with Mojang", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }

  @Override
  public JSONObject refresh(@NotNull String accessToken, @NotNull String clientToken) {
    val service = Executors.newSingleThreadExecutor();
    val future =
        service.submit(
            () ->
                Request.post(refreshUrl.toURI())
                    .bodyString(
                        new JSONObject()
                            .put("accessToken", accessToken)
                            .put("clientToken", clientToken)
                            .put("requestUser", true)
                            .toString(),
                        ContentType.APPLICATION_JSON)
                    .execute()
                    .handleResponse(
                        response -> {
                          val responseEntity = response.getEntity();
                          return responseEntity != null
                              ? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
                              : "{}";
                        }));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      LOGGER.error("Interrupted while refreshing with Mojang", ie);
    } catch (ExecutionException ee) {
      LOGGER.error("Error while refreshing with Mojang", ee.getCause());
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }
}
