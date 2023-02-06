package com.mojang;

import org.json.JSONObject;

public interface IYggdrasilAuthenticationService {

  JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  JSONObject validate(String accessToken, String clientToken);

  JSONObject refresh(String accessToken, String clientToken, boolean requestUser);
}
