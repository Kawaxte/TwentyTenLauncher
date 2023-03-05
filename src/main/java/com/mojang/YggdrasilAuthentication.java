package com.mojang;

import ee.twentyten.util.ConfigUtils;
import org.json.JSONObject;

public abstract class YggdrasilAuthentication {

  void getAndSetYggdrasilProfile(final String accessToken, final JSONObject loginResult) {
    String profileName = loginResult.getJSONObject("selectedProfile").getString("name");
    String profileId = loginResult.getJSONObject("selectedProfile").getString("id");
    ConfigUtils.getInstance().setYggdrasilProfileName(profileName);
    ConfigUtils.getInstance().setYggdrasilProfileId(profileId);
    ConfigUtils.getInstance().setYggdrasilSessionId(accessToken);
    ConfigUtils.getInstance().setMicrosoftSessionId(
        ConfigUtils.formatSessionId(ConfigUtils.getInstance().getClientToken(),
            ConfigUtils.getInstance().getYggdrasilAccessToken(),
            ConfigUtils.getInstance().getYggdrasilProfileId()));
  }

  public abstract void login();

  public abstract JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  public abstract JSONObject validate(String accessToken, String clientToken);

  public abstract JSONObject refresh(String accessToken, String clientToken, boolean requestUser);
}
