package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.MicrosoftAuthPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JOptionPane;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MicrosoftAuthTask implements Runnable {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(MicrosoftAuthTask.class);
  }

  private final ScheduledExecutorService service;
  private final String clientId;
  private final String deviceCode;

  public MicrosoftAuthTask(ScheduledExecutorService service, String clientId, String deviceCode) {
    this.service = service;
    this.clientId = clientId;
    this.deviceCode = deviceCode;
  }

  private static String[] getTokenResponse(JSONObject object) {
    if (object.has("error")) {
      val error = object.getString("error");
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

    val accessToken = object.getString("access_token");
    val refreshToken = object.getString("refresh_token");
    return new String[] {accessToken, refreshToken};
  }

  public static String[] getXBLTokenResponse(JSONObject object) {
    val displayClaims = object.getJSONObject("DisplayClaims");
    val xui = displayClaims.getJSONArray("xui");
    val uhs = xui.getJSONObject(0).getString("uhs");
    val token = object.getString("Token");
    return new String[] {uhs, token};
  }

  public static String getXSTSTokenResponse(JSONObject object) {
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

      LOGGER.error(object);
      return null;
    }
    return object.getString("Token");
  }

  public static String[] getAccessTokenResponse(JSONObject object) {
    val accessToken = object.getString("access_token");
    val expiresIn = object.getInt("expires_in");
    val expiresInMillis = System.currentTimeMillis() + (expiresIn * 1000L);
    return new String[] {accessToken, String.valueOf(expiresInMillis)};
  }

  private static boolean isItemNameEqualToGameMinecraft(JSONObject object) {
    val items = object.getJSONArray("items");
    if (!items.isEmpty()) {
      for (val item : items) {
        val name = ((JSONObject) item).getString("name");
        return Objects.equals(name, "game_minecraft");
      }
    }
    return false;
  }

  private static String[] getMinecraftProfileResponse(JSONObject object) {
    val id = object.getString("id");
    val name = object.getString("name");
    return new String[] {id, name};
  }

  @Override
  public void run() {
    if (!MicrosoftAuthPanel.instance.isShowing()) {
      service.shutdown();
    }

    val consumersToken = MicrosoftAuth.acquireToken(clientId, deviceCode);
    Objects.requireNonNull(consumersToken, "consumerToken cannot be null");
    val tokenResponse = getTokenResponse(consumersToken);
    if (tokenResponse == null) {
      return;
    }

    val userAuthenticate = MicrosoftAuth.acquireXBLToken(tokenResponse[0]);
    Objects.requireNonNull(userAuthenticate, "userAuthenticate cannot be null");
    val xblTokenResponse = getXBLTokenResponse(userAuthenticate);

    val xstsAuthorize = MicrosoftAuth.acquireXSTSToken(xblTokenResponse[1]);
    Objects.requireNonNull(xstsAuthorize, "xstsAuthorize cannot be null");
    val xstsTokenResponse = getXSTSTokenResponse(xstsAuthorize);

    val authenticateLoginWithXbox =
        MicrosoftAuth.acquireAccessToken(xblTokenResponse[0], xstsTokenResponse);
    Objects.requireNonNull(authenticateLoginWithXbox, "authenticateLoginWithXbox cannot be null");
    val accessTokenResponse = getAccessTokenResponse(authenticateLoginWithXbox);
    LauncherConfig.lookup.put("microsoftAccessToken", accessTokenResponse[0]);
    LauncherConfig.lookup.put("microsoftAccessTokenExpiresIn", accessTokenResponse[1]);
    LauncherConfig.lookup.put("microsoftRefreshToken", tokenResponse[1]);

    val entitlementsMcStore = MicrosoftAuth.checkEntitlementsMcStore(accessTokenResponse[0]);
    Objects.requireNonNull(entitlementsMcStore, "entitlementsMcStore cannot be null");
    val itemNameEqualToGameMinecraft = isItemNameEqualToGameMinecraft(entitlementsMcStore);
    if (!itemNameEqualToGameMinecraft) {
      val name = String.format("Player%s", System.currentTimeMillis() % 1000L);
      LauncherConfig.lookup.put("microsoftProfileDemo", true);
      LauncherConfig.lookup.put("microsoftProfileId", null);
      LauncherConfig.lookup.put("microsoftProfileName", name);
      LauncherConfig.saveConfig();

      // TODO: Call offline/demo instance of Minecraft.
      JOptionPane.showMessageDialog(
          LauncherPanel.instance,
          "You are not entitled to play full version of Minecraft.",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    val minecraftProfile = MicrosoftAuth.acquireMinecraftProfile(accessTokenResponse[0]);
    Objects.requireNonNull(minecraftProfile, "minecraftProfile cannot be null");
    val minecraftProfileResponse = getMinecraftProfileResponse(minecraftProfile);
    LauncherConfig.lookup.put("microsoftProfileDemo", false);
    LauncherConfig.lookup.put("microsoftProfileId", minecraftProfileResponse[0]);
    LauncherConfig.lookup.put("microsoftProfileName", minecraftProfileResponse[1]);
    LauncherConfig.saveConfig();

    // TODO: Call online instance of Minecraft.
    JOptionPane.showMessageDialog(
        LauncherPanel.instance,
        "You are entitled to play full version of Minecraft.",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);

    service.shutdown();
  }
}
