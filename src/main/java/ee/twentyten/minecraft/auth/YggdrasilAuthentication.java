package ee.twentyten.minecraft.auth;

import ee.twentyten.util.config.ConfigUtils;
import org.json.JSONObject;

public abstract class YggdrasilAuthentication {

  void getAndSetYggdrasilProfile(final String accessToken, final JSONObject result) {
    String profileName = result.getJSONObject("selectedProfile").getString("name");
    String profileId = result.getJSONObject("selectedProfile").getString("id");
    ConfigUtils.getInstance().setYggdrasilProfileName(profileName);
    ConfigUtils.getInstance().setYggdrasilProfileId(profileId);
    ConfigUtils.getInstance().setYggdrasilSessionId(
        ConfigUtils.formatSessionId(ConfigUtils.getInstance().getClientToken(), accessToken,
            profileId));
  }

  public abstract void login(String username, String password, boolean isPasswordSaved);

  public abstract JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  public abstract JSONObject validate(String accessToken, String clientToken);

  public abstract JSONObject refresh(String accessToken, String clientToken, boolean requestUser);
}
