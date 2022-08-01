package net.minecraft.auth.yggdrasil;

import net.minecraft.auth.AUtils;
import net.minecraft.launcher.LFrame;
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
                jsonParameters.put("agent", yggdrasilAgent.getAgentObject());
                jsonParameters.put("username", username);
                jsonParameters.put("password", password);
                jsonParameters.put("requestUser", true);

                JSONObject jsonResponse = AUtils.executePost("https://authserver.mojang.com/authenticate", String.valueOf(jsonParameters));
                if (jsonResponse.has("errorMessage")) {
                    switch (jsonResponse.getString("errorMessage")) {
                        case "Invalid credentials. Invalid username or password.":
                        case "Invalid credentials. Legacy account is non-premium account.":
                        case "Invalid credentials. Account migrated, use email as username.":
                            launcherFrame.getAuthPanel().setError("Login failed");
                            break;
                        case "Forbidden":
                            if (username.isEmpty()) {
                                launcherFrame.getAuthPanel().setError("Can't connect to minecraft.net");
                                launcherFrame.getAuthPanel().setNoNetwork();
                                return;
                            } else if (username.matches("^\\w+$") && username.length() > 2 && username.length() < 17) {
                                String sessionId = "mockToken" + ":" + "mockAccessToken" + ":" + "mockUUID";
                                launcherFrame.playOnline(username, sessionId);
                                System.out.println("Username is '" + username + "'");
                            } else {
                                launcherFrame.getAuthPanel().setError("Login failed");
                                return;
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
                        String sessionId = jsonResponse.getString("clientToken") + ":" + jsonResponse.getString("accessToken") + ":" + "mockUUID";
                        launcherFrame.playOnline(username, sessionId);
                        System.out.println("Username is '" + username + "'");
                    } else {
                        username = jsonResponse.getJSONObject("selectedProfile").getString("name");
                        String sessionId = jsonResponse.getString("clientToken") + ":"
                                + jsonResponse.getString("accessToken") + ":"
                                + jsonResponse.getJSONObject("selectedProfile").getString("id");
                        launcherFrame.playOnline(username, sessionId);
                        System.out.println("Username is '" + username + "'");
                    }
                }
            } catch (Exception e) {
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
        } catch (IOException iOException) {
            return false;
        }
    }
}