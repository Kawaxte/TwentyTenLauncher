package net.minecraft.yggdrasil;

import org.json.JSONObject;

public interface IYggdrasilAuthService {

  JSONObject authenticate(JSONObject payload);

  JSONObject refresh(JSONObject payload);

  JSONObject validate(JSONObject payload);
}
