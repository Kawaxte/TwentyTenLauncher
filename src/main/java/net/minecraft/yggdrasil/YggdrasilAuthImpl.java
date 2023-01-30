package net.minecraft.yggdrasil;

import org.json.JSONObject;

public class YggdrasilAuthImpl implements IYggdrasilAuthService {

  public static YggdrasilAuthImpl instance;

  public static YggdrasilAuthImpl getInstance() {
    if (instance == null) {
      instance = new YggdrasilAuthImpl();
    }
    return instance;
  }

  @Override
  public JSONObject authenticate(JSONObject payload) {
    return null;
  }

  @Override
  public JSONObject refresh(JSONObject payload) {
    return null;
  }

  @Override
  public JSONObject validate(JSONObject payload) {
    return null;
  }
}
