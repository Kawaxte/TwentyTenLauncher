package net.minecraft.authentication.mojang;

import ee.twentyten.config.Config;
import ee.twentyten.util.RequestManager;
import net.minecraft.util.YggdrasilManager;
import org.json.JSONObject;

public class YggdrasilAuthImpl implements IYggdrasilAuthService {

  @Override
  public JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser) {
    JSONObject payload = new JSONObject();
    payload.put("agent", YggdrasilAuthAgent.createAgent());
    payload.put("username", username);
    payload.put("password", password);
    payload.put("clientToken", clientToken);
    payload.put("requestUser", requestUser);
    return RequestManager.sendJsonRequest(
        String.format(YggdrasilManager.YGGDRASIL_AUTH_URL, "authenticate"), "POST",
        RequestManager.JSON_HEADER, payload);
  }

  @Override
  public JSONObject validate(String accessToken, String clientToken) {
    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    return RequestManager.sendJsonRequest(
        String.format(YggdrasilManager.YGGDRASIL_AUTH_URL, "validate"), "POST",
        RequestManager.JSON_HEADER, payload);
  }

  @Override
  public JSONObject refresh(String accessToken, String clientToken, boolean requestUser) {
    JSONObject selectedProfile = new JSONObject();
    selectedProfile.put("id", Config.instance.getProfileId());
    selectedProfile.put("name", Config.instance.getProfileName());

    JSONObject payload = new JSONObject();
    payload.put("accessToken", accessToken);
    payload.put("clientToken", clientToken);
    //payload.put("selectedProfile", selectedProfile);
    payload.put("requestUser", requestUser);
    return RequestManager.sendJsonRequest(
        String.format(YggdrasilManager.YGGDRASIL_AUTH_URL, "refresh"), "POST",
        RequestManager.JSON_HEADER, payload);
  }
}
