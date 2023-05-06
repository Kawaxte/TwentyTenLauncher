package io.github.kawaxte.twentyten.auth;

import com.sun.istack.internal.NotNull;
import io.github.kawaxte.twentyten.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.ui.LauncherPanel;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import lombok.val;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;

public class AbstractMojangAuthImpl extends AbstractMojangAuth {

  public static final AbstractMojangAuthImpl INSTANCE;

  static {
    INSTANCE = new AbstractMojangAuthImpl();
  }

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
                    .returnContent()
                    .asString(StandardCharsets.UTF_8));
    try {
      return new JSONObject(future.get());
    } catch (InterruptedException ie) {
      LOGGER.error("Interrupted while authenticating with Mojang", ie);
    } catch (ExecutionException ee) {
      val cause = ee.getCause();
      if (cause instanceof HttpResponseException) {
        LauncherUtils.addPanel(
            LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));
      } else {
        LOGGER.error("Error while authenticating with Mojang", cause);
      }
    } finally {
      service.shutdown();
    }
    return future.isDone() ? new JSONObject() : null;
  }

  @Override
  public JSONObject validate(@NotNull String accessToken, @NotNull String clientToken) {
    return null;
  }

  @Override
  public JSONObject refresh(@NotNull String accessToken, @NotNull String clientToken) {
    return null;
  }
}