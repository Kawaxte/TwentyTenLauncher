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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import net.minecraft.util.MinecraftUtils;
import org.json.JSONObject;

public class YggdrasilAuthenticationImpl extends YggdrasilAuthentication {

  @Override
  public void login() {
    final String username = LauncherLoginPanel.getInstance().getUsernameField().getText();
    final String password = new String(
        LauncherLoginPanel.getInstance().getPasswordField().getPassword());
    final boolean isPasswordSaved = LauncherLoginPanel.getInstance().getRememberPasswordCheckBox()
        .isSelected();

    ExecutorService loginService = Executors.newSingleThreadExecutor();
    loginService.submit(new Runnable() {
      @Override
      public void run() {
        final JSONObject loginResult = YggdrasilAuthenticationImpl.this.authenticate(username,
            password, ConfigUtils.getInstance().getClientToken(), true);

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            if (loginResult.has("error")) {
              LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
                  new LauncherNoNetworkPanel(),
                  LanguageUtils.getString(LanguageUtils.getBundle(),
                      "lp.label.errorLabel.loginFailed"));
              return;
            }

            String accessToken = loginResult.getString("accessToken");
            ConfigUtils.getInstance().setYggdrasilUsername(username);
            ConfigUtils.getInstance().setYggdrasilPassword(isPasswordSaved ? password : "");
            ConfigUtils.getInstance().setYggdrasilPasswordSaved(isPasswordSaved);
            ConfigUtils.getInstance().setYggdrasilAccessToken(accessToken);

            if (loginResult.has("selectedProfile")) {
              YggdrasilAuthenticationImpl.this.getAndSetYggdrasilProfile(accessToken, loginResult);

              ConfigUtils.writeToConfig();

              MinecraftUtils.launchMinecraft(ConfigUtils.getInstance().getYggdrasilProfileName(),
                  ConfigUtils.getInstance().getYggdrasilSessionId());
            } else {
              ConfigUtils.writeToConfig();

              MinecraftUtils.launchMinecraft();
            }
          }
        });
      }
    });
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
