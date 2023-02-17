package com.microsoft;

import com.microsoft.util.MicrosoftHelper;
import ee.twentyten.request.EMethod;
import ee.twentyten.ui.launcher.LauncherPanel;
import ee.twentyten.util.RequestHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class MicrosoftAuthenticationImpl extends MicrosoftAuthentication {

  private final String clientId;

  {
    this.clientId = "e1a4bd01-2c5f-4be0-8e6a-84d71929703b";
  }

  @Override
  public JSONObject acquireUserCode(LauncherPanel panel) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", this.clientId);
    data.put("scope", "XboxLive.signin offline_access");
    return RequestHelper.performJsonRequest(
        MicrosoftHelper.MSONLINE_DEVICE_CODE_URL, EMethod.POST,
        RequestHelper.xWwwFormUrlencodedHeader,
        MicrosoftHelper.ofFormData(data));
  }

  @Override
  public JSONObject acquireAccessToken(String deviceCode) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", this.clientId);
    data.put("device_code", deviceCode);
    data.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
    return RequestHelper.performJsonRequest(MicrosoftHelper.MSONLINE_TOKEN_URL,
        EMethod.POST, RequestHelper.xWwwFormUrlencodedHeader,
        MicrosoftHelper.ofFormData(data));
  }

  @Override
  public JSONObject acquireXblToken(String accessToken) {
    JSONObject properties = new JSONObject();
    properties.put("AuthMethod", "RPS");
    properties.put("SiteName", "user.auth.xboxlive.com");
    properties.put("RpsTicket", "d=" + accessToken);

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "http://auth.xboxlive.com");
    data.put("TokenType", "JWT");
    return RequestHelper.performJsonRequest(MicrosoftHelper.XBL_AUTH_URL,
        EMethod.POST, RequestHelper.jsonHeader, data);
  }

  @Override
  public JSONObject acquireXstsToken(String xblToken) {
    String[] userTokens = new String[]{xblToken};

    JSONObject properties = new JSONObject();
    properties.put("SandboxId", "RETAIL");
    properties.put("UserTokens", userTokens);

    JSONObject data = new JSONObject();
    data.put("Properties", properties);
    data.put("RelyingParty", "rp://api.minecraftservices.com/");
    data.put("TokenType", "JWT");
    return RequestHelper.performJsonRequest(MicrosoftHelper.XSTS_AUTH_URL,
        EMethod.POST, RequestHelper.jsonHeader, data);
  }

  @Override
  public JSONObject acquireMinecraftToken(String uhs, String xstsToken) {
    String identityToken = String.format("XBL3.0 x=%s;%s", uhs, xstsToken);

    JSONObject data = new JSONObject();
    data.put("identityToken", identityToken);
    return RequestHelper.performJsonRequest(
        MicrosoftHelper.MCSERVICES_LOGIN_URL, EMethod.POST,
        RequestHelper.jsonHeader, data);
  }

  @Override
  public JSONObject acquireMinecraftStore(String minecraftToken) {
    String bearer = String.format("Bearer %s", minecraftToken);

    Map<String, String> headers = new HashMap<>(
        Collections.singletonMap("Authorization", bearer));
    headers.putAll(RequestHelper.jsonHeader);
    return RequestHelper.performJsonRequest(
        MicrosoftHelper.MCSERVICES_STORE_URL, EMethod.GET, headers);
  }

  @Override
  public JSONObject acquireMinecraftProfile(String minecraftToken) {
    String bearer = String.format("Bearer %s", minecraftToken);

    Map<String, String> headers = new HashMap<>(
        Collections.singletonMap("Authorization", bearer));
    headers.putAll(RequestHelper.jsonHeader);
    return RequestHelper.performJsonRequest(
        MicrosoftHelper.MCSERVICES_PROFILE_URL, EMethod.GET, headers);
  }

  @Override
  public JSONObject refreshAccessToken(String refreshToken) {
    Map<Object, Object> data = new HashMap<>();
    data.put("client_id", this.clientId);
    data.put("refresh_token", refreshToken);
    data.put("grant_type", "refresh_token");
    data.put("scope", "XboxLive.signin offline_access");
    return RequestHelper.performJsonRequest(MicrosoftHelper.MSONLINE_TOKEN_URL,
        EMethod.POST, RequestHelper.xWwwFormUrlencodedHeader,
        MicrosoftHelper.ofFormData(data));
  }
}
