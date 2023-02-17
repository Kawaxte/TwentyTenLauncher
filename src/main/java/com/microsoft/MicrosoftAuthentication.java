package com.microsoft;

import ee.twentyten.ui.launcher.LauncherPanel;
import org.json.JSONObject;

public abstract class MicrosoftAuthentication {

  public abstract JSONObject acquireUserCode(LauncherPanel panel);

  public abstract JSONObject acquireAccessToken(String deviceCode);

  public abstract JSONObject acquireXblToken(String accessToken);

  public abstract JSONObject acquireXstsToken(String xblToken);

  public abstract JSONObject acquireMinecraftToken(String uhs,
      String xstsToken);

  public abstract JSONObject acquireMinecraftStore(
      String minecraftToken);

  public abstract JSONObject acquireMinecraftProfile(String minecraftToken);

  public abstract JSONObject refreshAccessToken(String refreshToken);
}
