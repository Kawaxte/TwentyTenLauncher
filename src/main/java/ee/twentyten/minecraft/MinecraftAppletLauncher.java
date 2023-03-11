package ee.twentyten.minecraft;

import lombok.Getter;
import lombok.Setter;

abstract class MinecraftAppletLauncher {

  @Getter
  @Setter
  public String username;

  public abstract void launch();

  public abstract void launch(String username, String sessionId);
}
