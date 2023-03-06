package com.mojang;

import com.mojang.util.YggdrasilUtils;
import ee.twentyten.request.EHeader;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherLoginPanel;
import ee.twentyten.ui.launcher.LauncherNoNetworkPanel;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.RequestUtils;
import net.minecraft.util.MinecraftUtils;
import org.json.JSONObject;

public class YggdrasilAuthenticationImpl extends YggdrasilAuthentication {

  @Override
  public void login() {
    String username = LauncherLoginPanel.getInstance().getUsernameField().getText();
    String password = new String(
        LauncherLoginPanel.getInstance().getPasswordField().getPassword());
    boolean isPasswordSaved = LauncherLoginPanel.getInstance().getRememberPasswordCheckBox()
        .isSelected();
    
    JSONObject loginResult = YggdrasilAuthenticationImpl.this.authenticate(username,
        password, ConfigUtils.getInstance().getClientToken(), true);
    if (loginResult.has("error")) {
      LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
          new LauncherNoNetworkPanel(),
          LanguageUtils.getString(LanguageUtils.getBundle(),
              "lp.label.errorLabel.loginFailed"));
      return;
    }

    ConfigUtils.getInstance().setYggdrasilUsername(username);
    ConfigUtils.getInstance().setYggdrasilPassword(isPasswordSaved ? password : "");
    ConfigUtils.getInstance().setYggdrasilPasswordSaved(isPasswordSaved);

    String accessToken = loginResult.getString("accessToken");
    ConfigUtils.getInstance().setYggdrasilAccessToken(accessToken);

    String[] availableProfiles = new String[loginResult.getJSONArray("availableProfiles").length()];
    if (availableProfiles.length == 0) {
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
    return RequestUtils.performJsonRequest(YggdrasilUtils.authserverAuthenticateUrl, EMethod.POST,
        EHeader.JSON.getHeader(), payload);
  }

  @Override
  public JSONObject validate(String accessToken, String clientToken) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    return RequestUtils.performJsonRequest(YggdrasilUtils.authserverValidateUrl, EMethod.POST,
        EHeader.JSON.getHeader(), payload);
  }

  @Override
  public JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);
    return RequestUtils.performJsonRequest(YggdrasilUtils.authserverRefreshUrl, EMethod.POST,
        EHeader.JSON.getHeader(), payload);
  }
}
