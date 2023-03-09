package ee.twentyten.minecraft;

import ee.twentyten.minecraft.update.ui.MinecraftUpdaterApplet;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.LauncherUtils;

public class MinecraftAppletLauncherImpl extends MinecraftAppletLauncher {

  @Override
  public void launch() {
    MinecraftUpdaterApplet gua = new MinecraftUpdaterApplet();
    gua.parameters.put("username", "Player");
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle(this.getTitle());
  }

  @Override
  public void launch(String username, String sessionId) {
    MinecraftUpdaterApplet gua = new MinecraftUpdaterApplet();
    gua.parameters.put("username", username);
    gua.parameters.put("sessionid", sessionId);
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle(this.getTitle());
  }
}
