package net.minecraft.authentication.mojang;

import org.json.JSONObject;

public interface IYggdrasilAuthService {

  JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  JSONObject validate(String accessToken, String clientToken);

  JSONObject refresh(String accessToken, String clientToken, boolean requestUser);
}
