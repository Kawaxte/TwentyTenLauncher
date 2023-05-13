package io.github.kawaxte.twentyten.launcher.auth;

import io.github.kawaxte.twentyten.launcher.Launcher;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.util.Objects;
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
    val authenticate = YggdrasilAuth.authenticate(username, password, clientToken);
    Objects.requireNonNull(authenticate, "authenticate cannot be null");
    val authenticateResponse = this.getAuthenticateResponse(authenticate);
    if (Objects.isNull(authenticateResponse)) {
      return;
    }

    LauncherConfig.lookup.put("mojangAccessToken", authenticateResponse[0]);

    if (this.isAvailableProfilesEmpty(authenticate)) {
      LauncherConfig.lookup.put("mojangProfileId", null);
      LauncherConfig.lookup.put("mojangProfileName", null);
      LauncherConfig.lookup.put("mojangProfileLegacy", false);
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(null, authenticateResponse[0], null);
    } else {
      LauncherConfig.lookup.put("mojangProfileId", authenticateResponse[1]);
      LauncherConfig.lookup.put("mojangProfileName", authenticateResponse[2]);
      LauncherConfig.lookup.put(
          "mojangProfileLegacy", this.isLegacyInSelectedProfile(authenticate));
      LauncherConfig.saveConfig();

      Launcher.launchMinecraft(
          authenticateResponse[2], authenticateResponse[0], authenticateResponse[1]);
    }
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
      LauncherUtils.swapContainers(
          LauncherPanel.instance, new LauncherNoNetworkPanel("lnnp.errorLabel.signin"));
      return null;
    }

    val accessToken = object.getString("accessToken");
    val selectedProfile = object.getJSONObject("selectedProfile");
    val id = selectedProfile.getString("id");
    val name = selectedProfile.getString("name");
    return new String[] {accessToken, id, name};
  }
}
