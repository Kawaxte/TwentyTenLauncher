package io.github.kawaxte.twentyten.launcher.auth;

import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

abstract class AbstractYggdrasilAuth {

  protected final Logger logger;
  protected URL authenticateUrl;
  protected URL validateUrl;
  protected URL refreshUrl;

  {
    this.logger = LogManager.getLogger(this);

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
      this.logger.error("Failed to create URL for Mojang API", ioe);
    }
  }

  public abstract JSONObject authenticate(String username, String password, String clientToken);

  public abstract JSONObject validateAccessToken(String accessToken, String clientToken);

  public abstract JSONObject refreshAccessToken(String accessToken, String clientToken);
}
