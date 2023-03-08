package net.minecraft;

import ee.twentyten.util.ConfigUtils;
import java.text.MessageFormat;

abstract class MinecraftLauncher {

  String getTitle() {
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
      default:
        break;
    }
    return MessageFormat.format("Minecraft {0}", selectedVersion);
  }

  public abstract void launch();

  public abstract void launch(String username, String sessionId);
}
