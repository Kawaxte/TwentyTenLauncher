package io.github.kawaxte.twentyten.auth;

import com.sun.istack.internal.NotNull;
import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.conf.LauncherConfig;
import io.github.kawaxte.twentyten.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.ui.LauncherPanel;
import io.github.kawaxte.twentyten.ui.MojangAuthPanel;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.util.Objects;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public final class MojangAuth {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(MojangAuth.class);
  }

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
    if (authenticate.has("error")) {
      LauncherUtils.addPanel(
          LauncherPanel.instance, new LauncherOfflinePanel("lop.errorLabel.signin"));
      return;
    }

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

  public static void validateAndRefresh() {
    val accessToken = AbstractLauncherConfigImpl.INSTANCE.getMojangAccessToken();
    val clientToken = AbstractLauncherConfigImpl.INSTANCE.getMojangClientToken();
    if ((Objects.isNull(accessToken) || Objects.isNull(clientToken))
        || (accessToken.isEmpty() || clientToken.isEmpty())) {
      LOGGER.warn("Access token or client token is null or empty");
      return;
    }

    JSONObject validate = AbstractMojangAuthImpl.INSTANCE.validate(accessToken, clientToken);
    if (validate.has("error")) {
      LOGGER.error(validate.getString("errorMessage"));

      val refresh = AbstractMojangAuthImpl.INSTANCE.refresh(accessToken, clientToken);
      if (refresh.has("error")) {
        LOGGER.error(refresh.getString("errorMessage"));
        return;
      }

      val newAccessToken = refresh.getString("accessToken");
      AbstractLauncherConfigImpl.INSTANCE.setMojangAccessToken(newAccessToken);
      LauncherConfig.saveConfig();
    }
  }
}
