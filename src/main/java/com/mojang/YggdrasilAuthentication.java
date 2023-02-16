package com.mojang;

import org.json.JSONObject;

public abstract class YggdrasilAuthentication {

  public abstract JSONObject authenticate(String username, String password,
      boolean requestUser);

  public abstract JSONObject validate(String accessToken, String clientToken);

  public abstract JSONObject refresh(String accessToken, String clientToken,
      boolean requestUser);
}
