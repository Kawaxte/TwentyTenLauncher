package io.github.kawaxte.twentyten.launcher.auth;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;
import static io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils.authInstance;

import com.sun.istack.internal.NotNull;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.signin.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JOptionPane;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MicrosoftAuthTask implements Runnable {

  private final ScheduledExecutorService service;
  private final String clientId;
  private final String deviceCode;
  private final Logger logger;

  {
    logger = LogManager.getLogger(this);
  }

  public MicrosoftAuthTask(
      @NotNull ScheduledExecutorService service,
      @NotNull String clientId,
      @NotNull String deviceCode) {
    this.service = service;
    this.clientId = clientId;
    this.deviceCode = deviceCode;
  }

  @Override
  public void run() {
    if (!MicrosoftAuthPanel.instance.isShowing()) {
      service.shutdown();
    }

    val consumerToken = authInstance.acquireToken(clientId, deviceCode);
    System.out.println(consumerToken);
    val tokenResponse = this.getTokenResponse(consumerToken);
    if (tokenResponse == null) {
      return;
    }

    val userAuthenticate = authInstance.acquireXBLToken(tokenResponse[0]);
    val xblTokenResponse = this.getXBLTokenResponse(userAuthenticate);

    val xstsAuthorize = authInstance.acquireXSTSToken(xblTokenResponse[1]);
    val xstsTokenResponse = this.getXSTSTokenResponse(xstsAuthorize);

    val authenticateLoginWithXbox =
        authInstance.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    val accessTokenResponse = this.getAccessTokenResponse(authenticateLoginWithXbox);
    configInstance.setMicrosoftAccessToken(accessTokenResponse[0]);
    configInstance.setMicrosoftAccessTokenExpiresIn(Long.parseLong(accessTokenResponse[1]));
    configInstance.setMicrosoftRefreshToken(tokenResponse[1]);

    val entitlementsMcStore = authInstance.acquireMinecraftStoreItems(accessTokenResponse[0]);
    val itemNameEqualToGameMinecraft = this.isItemNameEqualToGameMinecraft(entitlementsMcStore);
    if (!itemNameEqualToGameMinecraft) {
      configInstance.setMicrosoftProfileName(
          String.format("Player%s", System.currentTimeMillis() % 1000L));
      configInstance.setMicrosoftProfileDemo(true);
      configInstance.save();

      // TODO: Call offline/demo instance of Minecraft.
      JOptionPane.showMessageDialog(
          LauncherPanel.instance,
          "You are not entitled to play full version of Minecraft.",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    val minecraftProfile = authInstance.acquireMinecraftProfile(accessTokenResponse[0]);
    val minecraftProfileResponse = this.getMinecraftProfileResponse(minecraftProfile);
    configInstance.setMicrosoftProfileId(minecraftProfileResponse[0]);
    configInstance.setMicrosoftProfileName(minecraftProfileResponse[1]);
    configInstance.setMicrosoftProfileDemo(false);
    configInstance.save();

    // TODO: Call online instance of Minecraft.
    JOptionPane.showMessageDialog(
        LauncherPanel.instance,
        "You are entitled to play full version of Minecraft.",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);

    service.shutdown();
  }

  private String[] getMinecraftProfileResponse(JSONObject object) {
    val id = object.getString("id");
    val name = object.getString("name");
    return new String[] {id, name};
  }

  private boolean isItemNameEqualToGameMinecraft(JSONObject object) {
    val items = object.getJSONArray("items");
    if (!items.isEmpty()) {
      for (val item : items) {
        val name = ((JSONObject) item).getString("name");
        return Objects.equals(name, "game_minecraft");
      }
    }
    return false;
  }

  private String[] getAccessTokenResponse(JSONObject object) {
    val accessToken = object.getString("access_token");
    val expiresIn = String.valueOf(object.getInt("expires_in"));
    return new String[] {accessToken, expiresIn};
  }

  private String getXSTSTokenResponse(JSONObject object) {
    if (object.has("XErr")) {
      val xerr = object.getLong("XErr");
      switch ((int) xerr) {
        case (int) 2148916233L:
          LauncherUtils.addComponentToContainer(
              LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin_2148916233"));
          break;
        case (int) 2148916238L:
          LauncherUtils.addComponentToContainer(
              LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin_2148916238"));
          break;
        default:
          LauncherUtils.addComponentToContainer(
              LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));
          break;
      }

      logger.error(object);
      return null;
    }
    return object.getString("Token");
  }

  private String[] getXBLTokenResponse(JSONObject object) {
    val displayClaims = object.getJSONObject("DisplayClaims");
    val xui = displayClaims.getJSONArray("xui");
    val uhs = xui.getJSONObject(0).getString("uhs");
    val token = object.getString("Token");
    return new String[] {uhs, token};
  }

  private String[] getTokenResponse(JSONObject consumerToken) {
    if (consumerToken.has("error")) {
      val error = consumerToken.getString("error");
      if (Objects.equals(error, "authorization_pending")) {
        val progressBar = MicrosoftAuthPanel.instance.getExpiresInProgressBar();
        progressBar.setValue(progressBar.getValue() - 1);
        return null;
      }

      LauncherUtils.addComponentToContainer(
          LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));
      return null;
    }

    MicrosoftAuthPanel.instance.getCopyCodeLabel().setVisible(false);
    MicrosoftAuthPanel.instance.getUserCodeLabel().setVisible(false);
    MicrosoftAuthPanel.instance.getExpiresInProgressBar().setIndeterminate(true);
    MicrosoftAuthPanel.instance.getOpenBrowserButton().setVisible(false);

    val accessToken = consumerToken.getString("access_token");
    val refreshToken = consumerToken.getString("refresh_token");
    return new String[] {accessToken, refreshToken};
  }
}
