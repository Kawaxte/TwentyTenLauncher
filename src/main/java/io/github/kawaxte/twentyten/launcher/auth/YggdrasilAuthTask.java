package io.github.kawaxte.twentyten.launcher.auth;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;
import static io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils.authInstance;

import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import javax.swing.JOptionPane;
import lombok.val;
import org.json.JSONObject;

public class YggdrasilAuthTask implements Runnable {

  private final String username;
  private final String password;
  private final String clientToken;

  public YggdrasilAuthTask(String username, String password, String clientToken) {
    this.username = username;
    this.password = password;
    this.clientToken = clientToken;
  }

  @Override
  public void run() {
    val authenticate = authInstance.authenticate(username, password, clientToken);
    val authenticateResponse = this.getAuthenticateResponse(authenticate);
    if (authenticateResponse == null) {
      return;
    }
    configInstance.setMojangAccessToken(authenticateResponse[0]);

    if (this.isAvailableProfilesEmpty(authenticate)) {
      configInstance.setMojangProfileName(
          String.format("Player%s", System.currentTimeMillis() % 1000L));
      configInstance.setMojangProfileLegacy(false);
      configInstance.setMojangProfileDemo(true);
      configInstance.save();

      // TODO: Call offline/demo instance of Minecraft.
      JOptionPane.showMessageDialog(
          LauncherPanel.instance,
          "You are not entitled to play full version of Minecraft.",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    configInstance.setMojangProfileId(authenticateResponse[1]);
    configInstance.setMojangProfileName(authenticateResponse[2]);
    configInstance.setMojangProfileLegacy(this.isLegacyInSelectedProfile(authenticate));
    configInstance.setMojangProfileDemo(false);
    configInstance.save();

    // TODO: Call online instance of Minecraft.
    JOptionPane.showMessageDialog(
        LauncherPanel.instance,
        "You are entitled to play full version of Minecraft.",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private boolean isLegacyInSelectedProfile(JSONObject object) {
    val selectedProfile = object.getJSONObject("selectedProfile");
    return selectedProfile.has("legacy") && selectedProfile.getBoolean("legacy");
  }

  private boolean isAvailableProfilesEmpty(JSONObject object) {
    return object.getJSONArray("availableProfiles").isEmpty();
  }

  private String[] getAuthenticateResponse(JSONObject object) {
    if (object.has("error")) {
      LauncherUtils.addComponentToContainer(
          LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));
      return null;
    }

    val accessToken = object.getString("accessToken");
    val selectedProfile = object.getJSONObject("selectedProfile");
    val id = selectedProfile.getString("id");
    val name = selectedProfile.getString("name");
    return new String[] {accessToken, id, name};
  }
}
