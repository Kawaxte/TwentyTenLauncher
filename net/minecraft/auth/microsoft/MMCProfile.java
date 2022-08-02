package net.minecraft.auth.microsoft;

import net.minecraft.auth.AUtils;
import org.json.JSONObject;

public class MMCProfile {
    private final MAuthenticate microsoftAuthenticate;

    public MMCProfile(MAuthenticate microsoftAuthenticate) {
        this.microsoftAuthenticate = microsoftAuthenticate;
    }

    public void acquireMCProfile(String accessToken) {
        try {
            JSONObject jsonResponse = AUtils.excuteJSONGet(MAuthenticate.minecraftProfileUrl, accessToken);
            microsoftAuthenticate.getFrame().dispose();
            microsoftAuthenticate.getLauncherFrame().playOnline(jsonResponse.getString("name"),
                    ":mockToken:" + accessToken + jsonResponse.getString("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}