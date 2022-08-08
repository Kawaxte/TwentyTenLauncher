package net.minecraft.launcher.auth.yggdrasil;

import org.json.JSONObject;

public class YDAgent {
    public JSONObject getAgentObject() {
        JSONObject agentParameters = new JSONObject();
        agentParameters.put("name", "Minecraft").put("version", 1);
        return agentParameters;
    }
}
