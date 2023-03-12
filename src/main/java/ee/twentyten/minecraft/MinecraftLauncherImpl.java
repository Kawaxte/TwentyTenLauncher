package ee.twentyten.minecraft;

import ee.twentyten.minecraft.ui.MinecraftWrapper;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.LauncherUtils;

public class MinecraftLauncherImpl extends MinecraftLauncher {

  @Override
  public void launch() {
    this.setUsername("Player");

    MinecraftWrapper wrapper = new MinecraftWrapper("Player", null);
    wrapper.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), wrapper);

    wrapper.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }

  @Override
  public void launch(String username, String sessionId) {
    this.setUsername(username);

    MinecraftWrapper wrapper = new MinecraftWrapper(username, sessionId);
    wrapper.init();

    LauncherUtils.setContentPaneToContainer(LauncherPanel.getInstance(),
        LauncherFrame.getInstance(), wrapper);

    wrapper.start();
    LauncherFrame.getInstance().setTitle("Minecraft");
  }
}
