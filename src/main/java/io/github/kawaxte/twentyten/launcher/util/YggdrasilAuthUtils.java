package io.github.kawaxte.twentyten.launcher.util;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;

import com.sun.istack.internal.NotNull;
import io.github.kawaxte.twentyten.launcher.AbstractYggdrasilAuthImpl;
import io.github.kawaxte.twentyten.launcher.ui.LauncherOfflinePanel;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import java.util.Objects;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class YggdrasilAuthUtils {

  public static final AbstractYggdrasilAuthImpl YGGDRASIL_AUTH;
  static final Logger LOGGER;

  static {
    YGGDRASIL_AUTH = new AbstractYggdrasilAuthImpl();
    LOGGER = LogManager.getLogger(YggdrasilAuthUtils.class);
  }

  private YggdrasilAuthUtils() {}

  public static void authenticate(
      @NotNull String username, @NotNull String password, @NotNull String clientToken) {
    val rememberPasswordChecked =
        YggdrasilAuthPanel.instance.getRememberPasswordCheckBox().isSelected();

    CONFIG.setMojangUsername(username);
    CONFIG.setMojangPassword(rememberPasswordChecked ? password : "");
    CONFIG.setMojangRememberPasswordChecked(rememberPasswordChecked);

    val authenticate = YGGDRASIL_AUTH.authenticate(username, password, clientToken);
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

      CONFIG.setMojangProfileId(id);
      CONFIG.setMojangProfileName(name);
      CONFIG.setMojangProfileDemo(false);
      CONFIG.setMojangAccessToken(accessToken);
      CONFIG.save();

      // TODO: we would be launching minecraft here
      System.out.println("We did it!");
      return;
    }

    val name = String.format("Player%s", System.currentTimeMillis() % 1000L);

    CONFIG.setMojangProfileId(null);
    CONFIG.setMojangProfileName(name);
    CONFIG.setMojangProfileDemo(true);

    CONFIG.save();
    // TODO: offline instance
    System.out.println("We did it but offline!");
  }

  public static void refresh(@NotNull String accessToken, @NotNull String clientToken) {
    if ((Objects.isNull(accessToken) || Objects.isNull(clientToken))
        || (accessToken.isEmpty() || clientToken.isEmpty())) {
      return;
    }

    val validate = YGGDRASIL_AUTH.validate(accessToken, clientToken);
    if (!validate.has("error")) {
      return;
    }

    val refresh = YGGDRASIL_AUTH.refresh(accessToken, clientToken);
    if (refresh.has("error")) {
      LOGGER.error(refresh.getString("errorMessage"));
      return;
    }

    val newAccessToken = refresh.getString("accessToken");
    CONFIG.setMojangAccessToken(newAccessToken);
    CONFIG.save();
  }
}
