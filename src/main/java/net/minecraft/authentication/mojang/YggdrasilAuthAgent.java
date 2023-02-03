package net.minecraft.authentication.mojang;

import org.json.JSONObject;

class YggdrasilAuthAgent {

  private static final String AGENT_NAME;
  private static final int AGENT_VERSION;

  static {
    AGENT_NAME = "Minecraft";
    AGENT_VERSION = 1;
  }

  static JSONObject createAgent() {
    JSONObject agent = new JSONObject();
    agent.put("name", AGENT_NAME);
    agent.put("version", AGENT_VERSION);
    return agent;
  }
}
