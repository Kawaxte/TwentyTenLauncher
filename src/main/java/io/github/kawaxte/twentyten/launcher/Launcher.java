package io.github.kawaxte.twentyten.launcher;

import io.github.kawaxte.twentyten.launcher.ui.LauncherFrame;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import javax.swing.SwingUtilities;

public class Launcher {

  public static void main(String... args) {
    ELookAndFeel.setLookAndFeel();

    LauncherConfig.loadConfig();
    LauncherLanguage.loadLanguage("messages", ELanguage.getLanguage());

    SwingUtilities.invokeLater(() -> new LauncherFrame().setVisible(true));

    YggdrasilAuthUtils.validateAndRefreshAccessToken();
  }
}
