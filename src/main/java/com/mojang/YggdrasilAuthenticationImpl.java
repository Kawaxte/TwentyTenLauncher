package com.mojang;

import com.mojang.util.YggdrasilHelper;
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
  public JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("agent", this.getAgent());
    payload.put("username", username);
    payload.put("password", password);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    String authenticate = String.format(YggdrasilHelper.YGGDRASIL_AUTH_URL, "authenticate");
    return RequestHelper.performJsonRequest(authenticate, "POST", RequestHelper.jsonHeader, true,
        payload);
  }

  @Override
  public JSONObject validate(String accessToken, String clientToken) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);

    String validate = String.format(YggdrasilHelper.YGGDRASIL_AUTH_URL, "validate");
    return RequestHelper.performJsonRequest(validate, "POST", RequestHelper.jsonHeader, true,
        payload);
  }

  @Override
  public JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);

    String refresh = String.format(YggdrasilHelper.YGGDRASIL_AUTH_URL, "refresh");
    return RequestHelper.performJsonRequest(refresh, "POST", RequestHelper.jsonHeader, true,
        payload);
  }
}
