package net.minecraft.launcher;

import net.minecraft.MCUtils;
import org.json.JSONObject;

public final class LauncherUpdate {
    public static final String currentVersion = "0.8.0822";
    public static final String latestVersion = isOutdated();

    private static String isOutdated() {
        try {
            JSONObject apiResponse = MCUtils.requestMethod("https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest", "GET", "{}");
            return apiResponse != null ? apiResponse.getString("tag_name") : null;
        } catch (Exception e) {
            e.printStackTrace();
            return currentVersion;
        }
    }
}
