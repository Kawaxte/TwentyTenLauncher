package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.util.Objects;
import javax.swing.SwingUtilities;
import lombok.val;

public class Launcher {

  public static void main(String... args) {
    ELookAndFeel.setLookAndFeel();

    LauncherConfig.loadConfig();
    LauncherLanguage.loadLanguage(
        "messages",
        Objects.nonNull(LauncherConfig.lookup.get("selectedLanguage"))
            ? (String) LauncherConfig.lookup.get("selectedLanguage")
            : ELanguage.USER_LANGUAGE);

    SwingUtilities.invokeLater(() -> new LauncherFrame().setVisible(true));

    val microsoftAccessTokenExpired = MicrosoftAuthUtils.isAccessTokenExpired();
    val mojangAccessTokenExpired = YggdrasilAuthUtils.isAccessTokenExpired();
    if (mojangAccessTokenExpired) {
      YggdrasilAuthUtils.refreshAccessToken();
    }
    if (microsoftAccessTokenExpired) {
      MicrosoftAuthUtils.refreshAccessToken();
    }
  }
}
