package net.minecraft.auth.yggdrasil;

import org.json.JSONObject;

class YggdrasilAuthAgent {

  private static final String name;
  private static final int version;

  static {
    name = "Minecraft";
    version = 1;
  }

  static JSONObject agent() {
    JSONObject agent = new JSONObject();
    agent.put("name", name);
    agent.put("version", version);
    return agent;
  }
}
