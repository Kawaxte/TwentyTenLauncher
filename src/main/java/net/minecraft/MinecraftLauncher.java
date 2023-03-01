package net.minecraft;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LauncherUtils;
import java.text.MessageFormat;
import net.minecraft.update.ui.MinecraftUpdaterApplet;

public class MinecraftLauncher {

  private String formatTitle() {
    String selectedVersion = ConfigUtils.getInstance().getSelectedVersion();
    switch (selectedVersion.charAt(0)) {
      case 'b':
        selectedVersion = MessageFormat.format("Beta {0}", selectedVersion.substring(1));
        break;
      case 'a':
        selectedVersion = MessageFormat.format("Alpha v{0}", selectedVersion.substring(1));
        break;
      case 'i':
        selectedVersion = "Infdev";
        break;
    }
    return MessageFormat.format("Minecraft {0}", selectedVersion);
  }

  public void launch() {
    MinecraftUpdaterApplet updaterApplet = new MinecraftUpdaterApplet();
    updaterApplet.parameters.put("username", "Player");
    this.initAndStart(updaterApplet);
  }

  public void launch(String username, String sessionId) {
    MinecraftUpdaterApplet updaterApplet = new MinecraftUpdaterApplet();
    updaterApplet.parameters.put("username", username);
    updaterApplet.parameters.put("sessionid", sessionId);
    this.initAndStart(updaterApplet);
  }

  private void initAndStart(MinecraftUpdaterApplet mua) {
    mua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), mua);

    mua.start();
    LauncherFrame.getInstance().setTitle(this.formatTitle());
  }
}
