package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import lombok.val;

public class Launcher {

  public static void main(String... args) {
    ELookAndFeel.setLookAndFeel();

    LauncherConfig.loadConfig();
    LauncherLanguage.loadLanguage("messages", ELanguage.getLanguage());

    SwingUtilities.invokeLater(
        () -> {
          new LauncherFrame().setVisible(true);

          Arrays.stream(YggdrasilAuthPanel.instance.getComponents())
              .forEachOrdered(component -> component.setEnabled(false));
        });

    val microsoftAccessTokenExpired = MicrosoftAuthUtils.isAccessTokenExpired();
    val mojangAccessTokenExpired = YggdrasilAuthUtils.isAccessTokenExpired();
    if (microsoftAccessTokenExpired) {
      MicrosoftAuthUtils.refreshAccessToken();
    }
    if (mojangAccessTokenExpired) {
      YggdrasilAuthUtils.refreshAccessToken();
    }

    Arrays.stream(YggdrasilAuthPanel.instance.getComponents())
        .forEachOrdered(component -> component.setEnabled(true));
  }
}
