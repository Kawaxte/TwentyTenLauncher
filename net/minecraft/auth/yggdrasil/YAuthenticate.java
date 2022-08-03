package net.minecraft.auth.yggdrasil;

import net.minecraft.auth.AUtils;
import net.minecraft.launcher.LFrame;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

public class YAuthenticate implements Serializable {
    public final YAgent yggdrasilAgent = new YAgent();
    private final LFrame launcherFrame;

    public YAuthenticate(LFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    public void authenticate(String username, String password) {
        if (!(hasNetwork())) {
            launcherFrame.getAuthPanel().setError("Can't connect to minecraft.net");
            launcherFrame.getAuthPanel().setNoNetwork();
        } else {
            try {
                JSONObject jsonParameters = new JSONObject();
                jsonParameters.put("agent", yggdrasilAgent.getAgentObject()).put("username", username).put("password", password).put("requestUser", true);

                JSONObject jsonResponse = AUtils.requestJSONPOST("https://authserver.mojang.com/authenticate", String.valueOf(jsonParameters));
                if (jsonResponse.has("errorMessage")) {
                    switch (jsonResponse.getString("errorMessage")) {
                        case "Invalid credentials. Invalid username or password.":
                        case "Invalid credentials. Legacy account is non-premium account.":
                        case "Invalid credentials. Account migrated, use email as username.":
                            launcherFrame.getAuthPanel().setError("Login failed");
                            break;
                        case "Forbidden":
                            if (!username.matches("^\\w+$") || username.length() <= 2 || username.length() >= 17) {
                                if (username.isEmpty()) {
                                    launcherFrame.getAuthPanel().setError("Can't connect to minecraft.net");
                                    launcherFrame.getAuthPanel().setNoNetwork();
                                    return;
                                } else if ("$MS".equals(username)) {
                                    launcherFrame.getMicrosoftAuthenticate();
                                } else {
                                    launcherFrame.getAuthPanel().setError("Login failed");
                                    return;
                                }
                            } else {
                                launcherFrame.playOnline(username, "mockToken:mockAccessToken:mockUUID");
                                System.out.println("Username is '" + username + "'");
                            }
                            break;
                        case "Migrated":
                            launcherFrame.getAuthPanel().setError("Migrated");
                            break;
                        default:
                            launcherFrame.getAuthPanel().setError(String.valueOf(jsonResponse));
                            launcherFrame.getAuthPanel().setNoNetwork();
                            break;
                    }
                } else {
                    if (jsonResponse.getJSONArray("availableProfiles").length() == 0) {
                        launcherFrame.playOnline(username, String.format("%s:%s:mockUUID",
                                jsonResponse.getString("clientToken"),
                                jsonResponse.getString("accessToken")));
                    } else {
                        username = jsonResponse.getJSONObject("selectedProfile").getString("name");
                        launcherFrame.playOnline(username, String.format("%s:%s:%s",
                                jsonResponse.getString("clientToken"),
                                jsonResponse.getString("accessToken"),
                                jsonResponse.getJSONObject("selectedProfile").getString("id")));
                    }
                    System.out.println("Username is '" + username + "'");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                launcherFrame.getAuthPanel().setError(e.toString());
                launcherFrame.getAuthPanel().setNoNetwork();
            }
        }
    }

    public LFrame getLauncherFrame() {
        return launcherFrame;
    }

    public boolean hasNetwork() {
        try {
            URL url = new URL("https://minecraft.net/");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}