package com.mojang;

import com.mojang.util.YggdrasilHelper;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.request.EMethod;
import ee.twentyten.util.RequestHelper;
import org.json.JSONObject;

public class YggdrasilAuthenticationImpl extends YggdrasilAuthentication {

  public JSONObject getAgent() {
    JSONObject agent = new JSONObject();
    agent.put("name", "Minecraft");
    agent.put("version", 1);
    return agent;
  }

  @Override
  public JSONObject authenticate(String username, String password,
      boolean requestUser) {
    String clientToken = LauncherConfig.instance.getClientToken();

    JSONObject payload = new JSONObject();
    payload.put("agent", this.getAgent());
    payload.put("username", username);
    payload.put("password", password);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    String authenticate = String.format(YggdrasilHelper.MOJANG_SERVER_URL,
        "authenticate");
    return RequestHelper.performJsonRequest(authenticate, EMethod.POST,
        RequestHelper.jsonHeader, payload);
  }

  @Override
  public JSONObject validate(String accessToken, String clientToken) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);

    String validate = String.format(YggdrasilHelper.MOJANG_SERVER_URL,
        "validate");
    return RequestHelper.performJsonRequest(validate, EMethod.POST,
        RequestHelper.jsonHeader, payload);
  }

  @Override
  public JSONObject refresh(String accessToken, String clientToken,
      boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    String refresh = String.format(YggdrasilHelper.MOJANG_SERVER_URL,
        "refresh");
    return RequestHelper.performJsonRequest(refresh, EMethod.POST,
        RequestHelper.jsonHeader, payload);
  }
}
