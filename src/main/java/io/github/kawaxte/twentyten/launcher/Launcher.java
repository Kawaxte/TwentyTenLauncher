package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.ui.YggdrasilAuthPanel;
import io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.SwingUtilities;

public class Launcher {

  public static void main(String... args) {
    ELookAndFeel.setLookAndFeel();

    LauncherConfig.loadConfig();
    LauncherLanguage.loadLanguage("messages", ELanguage.getLanguage());

    SwingUtilities.invokeLater(
        () -> {
          new LauncherFrame().setVisible(true);

          if (Objects.nonNull(YggdrasilAuthPanel.instance)) {
            Arrays.stream(YggdrasilAuthPanel.instance.getComponents())
                .forEachOrdered(component -> component.setEnabled(false));
          }
        });

    MicrosoftAuthUtils.checkAndRefreshAccessToken();
    YggdrasilAuthUtils.validateAndRefreshAccessToken();
  }
}
