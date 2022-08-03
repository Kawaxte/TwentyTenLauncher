package net.minecraft.auth.yggdrasil;

import org.json.JSONObject;

import java.io.Serializable;

public class YAgent implements Serializable {
    public JSONObject getAgentObject() {
        JSONObject agentParameters = new JSONObject();
        agentParameters.put("name", "Minecraft").put("version", 1);
        return agentParameters;
    }
}