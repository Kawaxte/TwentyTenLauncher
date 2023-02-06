package com.mojang;

import org.json.JSONObject;

public abstract class YggdrasilAuthentication implements IYggdrasilAuthenticationService {

  public abstract JSONObject getAgent();

  public abstract JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  public abstract JSONObject validate(String accessToken, String clientToken);

  public abstract JSONObject refresh(String accessToken, String clientToken, boolean requestUser);
}
