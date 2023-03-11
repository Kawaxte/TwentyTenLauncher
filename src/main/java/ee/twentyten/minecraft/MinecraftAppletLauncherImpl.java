package ee.twentyten.minecraft;

import ee.twentyten.minecraft.update.ui.MinecraftUpdaterApplet;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.launcher.LauncherUtils;

public class MinecraftAppletLauncherImpl extends MinecraftAppletLauncher {

  @Override
  public void launch() {
    this.setUsername("Player");

    MinecraftUpdaterApplet gua = new MinecraftUpdaterApplet("Player", null);
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }

  @Override
  public void launch(String username, String sessionId) {
    this.setUsername(username);

    MinecraftUpdaterApplet gua = new MinecraftUpdaterApplet(username, sessionId);
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }
}
