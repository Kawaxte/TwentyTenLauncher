package net.minecraft;

import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.LauncherUtils;
import net.minecraft.update.ui.GameUpdaterApplet;

public class MinecraftLauncherImpl extends MinecraftLauncher {

  @Override
  public void launch() {
    GameUpdaterApplet gua = new GameUpdaterApplet();
    gua.parameters.put("username", "Player");
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle(this.getTitle());
  }

  @Override
  public void launch(String username, String sessionId) {
    GameUpdaterApplet gua = new GameUpdaterApplet();
    gua.parameters.put("username", username);
    gua.parameters.put("sessionid", sessionId);
    gua.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), gua);

    gua.start();
    LauncherFrame.getInstance().setTitle(this.getTitle());
  }
}
