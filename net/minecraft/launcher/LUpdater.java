package net.minecraft.launcher;

import org.json.JSONObject;

import java.io.Serializable;

public class LUpdater implements Serializable {
    public static String currentVersion = "0.8.0122";
    public static String latestVersion = isOutdated();

    public static String isOutdated() {
        String version = String.valueOf(LUtils.executeGet("https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest"));
        return new JSONObject(version).getString("tag_name");
    }
}