package net.minecraft.launcher;

import org.json.JSONObject;

import javax.swing.JOptionPane;
import java.io.Serializable;

public final class LUpdater implements Serializable {
    private static final long serialVersionUID = 1L;
    static final String versionUrl = "https://api.github.com/repos/sojlabjoi/AlphacraftLauncher/releases/latest";
    public static final String currentVersion = "0.8.0422";
    public static final String latestVersion = isOutdated();

    private LUpdater() {
        throw new UnsupportedOperationException();
    }

    static String isOutdated() {
        String response = String.valueOf(LUtils.requestJSONGET(versionUrl));
        if (response.contains("tag_name")) {
            return new JSONObject(response).getString("tag_name");
        } else {
            JOptionPane.showMessageDialog(null,
                    "Failed to check for updates. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            return currentVersion;
        }
    }
}