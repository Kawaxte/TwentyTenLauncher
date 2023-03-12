package ee.twentyten.auth;

import ee.twentyten.request.ConnectionRequest;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.DiscordUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.MinecraftUtils;
import ee.twentyten.util.RequestUtils;
import ee.twentyten.util.YggdrasilUtils;
import org.json.JSONObject;

public class YggdrasilAuthenticationImpl extends YggdrasilAuthentication {

  @Override
  public void login(String username, String password, boolean isPasswordSaved) {
    DiscordUtils.updateRichPresence("Logging in with Yggdrasil");

    JSONObject loginResult = YggdrasilAuthenticationImpl.this.authenticate(username, password,
        ConfigUtils.getInstance().getClientToken(), true);
    if (loginResult.has("error")) {
      LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(),
          LanguageUtils.getString(LanguageUtils.getBundle(), "lp.label.errorLabel.loginFailed"));
      return;
    }

    ConfigUtils.getInstance().setYggdrasilUsername(username);
    ConfigUtils.getInstance().setYggdrasilPassword(isPasswordSaved ? password : "");
    ConfigUtils.getInstance().setYggdrasilPasswordSaved(isPasswordSaved);

    String accessToken = loginResult.getString("accessToken");
    ConfigUtils.getInstance().setYggdrasilAccessToken(accessToken);

    String[] availableProfiles = new String[loginResult.getJSONArray("availableProfiles").length()];
    if (availableProfiles.length == 0) {
      ConfigUtils.getInstance().setYggdrasilProfileId(null);
      ConfigUtils.getInstance().setYggdrasilProfileName(null);

      ConfigUtils.writeToConfig();

      MinecraftUtils.launchMinecraft();
      return;
    }

    YggdrasilAuthenticationImpl.this.getAndSetYggdrasilProfile(accessToken, loginResult);

    ConfigUtils.writeToConfig();

    MinecraftUtils.launchMinecraft(ConfigUtils.getInstance().getYggdrasilProfileName(),
        ConfigUtils.getInstance().getYggdrasilSessionId());
  }

  @Override
  public JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject agent = new JSONObject();
    agent.put("name", "Minecraft");
    agent.put("version", 1);

    JSONObject payload = new JSONObject();
    payload.put("agent", agent);
    payload.put("username", username);
    payload.put("password", password);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    return new ConnectionRequest.Builder()
        .setUrl(YggdrasilUtils.authserverAuthenticateUrl)
        .setMethod(EMethod.POST)
        .setHeaders(RequestUtils.JSON)
        .setBody(payload)
        .setSSLSocketFactory(RequestUtils.getSSLSocketFactory())
        .build().performJsonRequest();
  }

  @Override
  public JSONObject validate(String accessToken, String clientToken) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);

    return new ConnectionRequest.Builder()
        .setUrl(YggdrasilUtils.authserverValidateUrl)
        .setMethod(EMethod.POST)
        .setHeaders(RequestUtils.JSON)
        .setBody(payload)
        .setSSLSocketFactory(RequestUtils.getSSLSocketFactory())
        .build().performJsonRequest();
  }

  @Override
  public JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    return new ConnectionRequest.Builder()
        .setUrl(YggdrasilUtils.authserverRefreshUrl)
        .setMethod(EMethod.POST)
        .setHeaders(RequestUtils.JSON)
        .setBody(payload)
        .setSSLSocketFactory(RequestUtils.getSSLSocketFactory())
        .build().performJsonRequest();
  }
}
