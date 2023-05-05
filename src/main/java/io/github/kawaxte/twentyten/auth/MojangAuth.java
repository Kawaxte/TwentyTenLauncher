package io.github.kawaxte.twentyten.auth;

import com.sun.istack.internal.NotNull;
import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.conf.LauncherConfig;
import io.github.kawaxte.twentyten.ui.MojangAuthPanel;
import lombok.val;
import org.json.JSONObject;

public final class MojangAuth {

  private MojangAuth() {}

  public static void authenticate(
      @NotNull String username, @NotNull String password, @NotNull String clientToken) {
    val mojangRememberPasswordChecked =
        MojangAuthPanel.instance.getRememberPasswordCheckBox().isSelected();

    AbstractLauncherConfigImpl.INSTANCE.setMojangUsername(username);
    AbstractLauncherConfigImpl.INSTANCE.setMojangPassword(
        mojangRememberPasswordChecked ? password : "");
    AbstractLauncherConfigImpl.INSTANCE.setMojangRememberPasswordChecked(
        mojangRememberPasswordChecked);

    val authenticate =
        AbstractMojangAuthImpl.INSTANCE.authenticate(username, password, clientToken);
    if (authenticate != null && !authenticate.isEmpty()) {
      val accessToken = authenticate.getString("accessToken");
      val availableProfiles = authenticate.getJSONArray("availableProfiles");
      if (!availableProfiles.isEmpty()) {
        val selectedProfile = authenticate.getJSONObject("selectedProfile");
        val id = selectedProfile.getString("id");
        val name = selectedProfile.getString("name");

        AbstractLauncherConfigImpl.INSTANCE.setMojangProfileId(id);
        AbstractLauncherConfigImpl.INSTANCE.setMojangProfileName(name);
        AbstractLauncherConfigImpl.INSTANCE.setMojangProfileDemo(false);
        AbstractLauncherConfigImpl.INSTANCE.setMojangAccessToken(accessToken);
        LauncherConfig.saveConfig();

        // TODO: we would be launching minecraft here
        System.out.println("We did it!");
        return;
      }

      val name = String.format("Player%s", System.currentTimeMillis() % 1000L);

      AbstractLauncherConfigImpl.INSTANCE.setMojangProfileId(null);
      AbstractLauncherConfigImpl.INSTANCE.setMojangProfileName(name);
      AbstractLauncherConfigImpl.INSTANCE.setMojangProfileDemo(true);

      LauncherConfig.saveConfig();
      // TODO: offline instance
      System.out.println("We did it but offline!");
    }
  }

  public static JSONObject validate(@NotNull String accessToken, @NotNull String clientToken) {
    return AbstractMojangAuthImpl.INSTANCE.validate(accessToken, clientToken);
  }

  public static JSONObject refresh(@NotNull String accessToken, @NotNull String clientToken) {
    return AbstractMojangAuthImpl.INSTANCE.refresh(accessToken, clientToken);
  }
}
