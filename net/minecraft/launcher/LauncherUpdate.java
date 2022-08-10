package net.minecraft.launcher;

import net.minecraft.MCUtils;
import org.json.JSONObject;

import javax.swing.JOptionPane;

public final class LauncherUpdate {
    public static final String currentVersion = "0.8.1022";
    public static final String latestVersion = isOutdated();

    private LauncherUpdate() {
        MCUtils.setSystemLookAndFeel();
    }

    private static String isOutdated() {
        try {
            JSONObject apiResponse = MCUtils.requestMethod("https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest", "GET", "{}");
            return apiResponse.getString("tag_name");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "An error occurred while checking for updates.", "Error", JOptionPane.ERROR_MESSAGE);
            return currentVersion;
        }
    }
}
