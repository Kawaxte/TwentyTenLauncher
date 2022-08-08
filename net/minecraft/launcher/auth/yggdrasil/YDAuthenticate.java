package net.minecraft.launcher.auth.yggdrasil;

import net.minecraft.MCUtils;
import net.minecraft.launcher.LauncherFrame;
import net.minecraft.launcher.LauncherUpdate;
import net.minecraft.launcher.auth.AuthLastLogin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class YDAuthenticate {
    private final YDAgent yggdrasilAgent = new YDAgent();
    private final LauncherFrame launcherFrame;

    public YDAuthenticate(LauncherFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    public void authenticate(String username, String password, String clientToken) {
        JSONObject parameters = new JSONObject();
        parameters.put("agent", yggdrasilAgent.getAgentObject());
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("clientToken", clientToken);
        try {
            JSONObject authResponse = MCUtils.requestMethod("https://authserver.mojang.com/authenticate", "POST", String.valueOf(parameters));
            if (!(LauncherUpdate.latestVersion != null && LauncherUpdate.latestVersion.matches(LauncherUpdate.currentVersion))) {
                this.launcherFrame.showError("Outdated launcher");
                this.launcherFrame.getAuthPanel().setNoNetwork();
                return;
            }
            if (authResponse != null) {
                if (!authResponse.has("error")) {
                    if (!authResponse.has("selectedProfile")) {
                        this.launcherFrame.showError("User not premium");
                        this.launcherFrame.getAuthPanel().setNoNetwork();
                        return;
                    }
                    String name = authResponse.getJSONObject("selectedProfile").getString("name");
                    String accessToken = authResponse.getString("accessToken");
                    String uuid = authResponse.getJSONObject("selectedProfile").getString("id");
                    this.launcherFrame.getOnlineInstance(name, String.format("%s:%s:%s", clientToken, accessToken, uuid));
                    System.out.println("Username is '" + username + "'");
                    AuthLastLogin.writeLastLogin(username, clientToken, accessToken, uuid);
                } else {
                    switch (authResponse.getString("error")) {
                        case "ForbiddenOperationException":
                            this.launcherFrame.showError("Login failed");
                            break;
                        case "GoneException":
                            this.launcherFrame.showError("Migrated");
                            break;
                        default:
                            this.launcherFrame.showError(authResponse.getString("error"));
                            break;
                    }
                    this.launcherFrame.authPanel.setNoNetwork();
                }
            } else {
                this.launcherFrame.showError("Can't connect to minecraft.net");
                this.launcherFrame.authPanel.setNoNetwork();
            }
        } catch (IOException | JSONException e) {
            if (e.getMessage().contains("authserver.mojang.com")) {
                this.launcherFrame.showError("Can't connect to minecraft.net");
                this.launcherFrame.getAuthPanel().setNoNetwork();
                return;
            }
            e.printStackTrace();
        }
    }
}
