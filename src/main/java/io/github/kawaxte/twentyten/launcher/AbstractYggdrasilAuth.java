package io.github.kawaxte.twentyten.launcher;

import com.sun.istack.internal.NotNull;
import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

abstract class AbstractYggdrasilAuth {

  static final Logger LOGGER;
  static URL authenticateUrl;
  static URL validateUrl;
  static URL refreshUrl;

  static {
    LOGGER = LogManager.getLogger(AbstractYggdrasilAuth.class);

    try {
      authenticateUrl =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("authenticate")
                  .toString());
      validateUrl =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("validate")
                  .toString());
      refreshUrl =
          new URL(
              new StringBuilder()
                  .append("https://authserver.mojang.com/")
                  .append("refresh")
                  .toString());
    } catch (IOException ioe) {
      LOGGER.error("Failed to create URL for Mojang API", ioe);
    }
  }

  JSONObject getAgent() {
    return new JSONObject().put("name", "Minecraft").put("version", 1);
  }

  public abstract JSONObject authenticate(
      @NotNull String username, @NotNull String password, @NotNull String clientToken);

  public abstract JSONObject validate(@NotNull String accessToken, @NotNull String clientToken);

  public abstract JSONObject refresh(@NotNull String accessToken, @NotNull String clientToken);
}
