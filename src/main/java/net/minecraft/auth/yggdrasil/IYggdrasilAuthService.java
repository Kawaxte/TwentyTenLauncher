package net.minecraft.auth.yggdrasil;

import org.json.JSONObject;

public interface IYggdrasilAuthService {

  JSONObject authenticate(String username, String password, String clientToken,
      boolean requestUser);

  JSONObject refresh(String accessToken, String clientToken, boolean requestUser);

  JSONObject validate(String accessToken, String clientToken);
}
