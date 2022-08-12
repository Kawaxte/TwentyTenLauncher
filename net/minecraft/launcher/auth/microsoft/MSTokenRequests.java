package net.minecraft.launcher.auth.microsoft;

import net.minecraft.MCUtils;
import org.json.JSONObject;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.HashMap;

public class MSTokenRequests {
    private final MSAuthenticate microsoftAuthenticate;

    public MSTokenRequests(MSAuthenticate microsoftAuthenticate) {
        this.microsoftAuthenticate = microsoftAuthenticate;
        MCUtils.setSystemLookAndFeel();
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public void getAccessToken(String authCode) {
        try {
            HashMap<Object, Object> parameters = new HashMap<>();
            parameters.put("client_id", "00000000402b5328");
            parameters.put("code", authCode);
            parameters.put("grant_type", "authorization_code");
            parameters.put("redirect_uri", MSAuthenticate.loaDesktopUrl);
            parameters.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.loaTokenUrl, "POST", MSFormData.encodeFormData(parameters));
            String access_token = tokenResponse.getString("access_token");
            getXBLToken(access_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getXBLToken(String access_token) {
        try {
            JSONObject propertyParameters = new JSONObject();
            propertyParameters.put("AuthMethod", "RPS");
            propertyParameters.put("SiteName", "user.auth.xboxlive.com");
            propertyParameters.put("RpsTicket", access_token);

            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("Properties", propertyParameters);
            jsonParameters.put("RelyingParty", "http://auth.xboxlive.com");
            jsonParameters.put("TokenType", "JWT");

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.xblUserAuthUrl, "POST", jsonParameters.toString());
            getXSTSToken(tokenResponse.getString("Token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getXSTSToken(String token) {
        JSONObject tokenResponse = new JSONObject();
        try {
            JSONObject propertyParameters = new JSONObject();
            propertyParameters.put("SandboxId", "RETAIL");
            propertyParameters.put("UserTokens", new String[]{token});

            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("Properties", propertyParameters);
            jsonParameters.put("RelyingParty", "rp://api.minecraftservices.com/");
            jsonParameters.put("TokenType", "JWT");

            tokenResponse = MCUtils.requestMethod(MSAuthenticate.xblXstsAuthurl, "POST", jsonParameters.toString());
            String uhs = tokenResponse.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            String xstsToken = tokenResponse.getString("Token");
            getMinecraftAccessToken(uhs, xstsToken);
        } catch (IOException e) {
            switch (tokenResponse.getString("XErr")) {
                case "2148916233":
                    JOptionPane.showMessageDialog(null, "This account does not have an Xbox account");
                    microsoftAuthenticate.launcherFrame.showError("Login failed");
                    break;
                case "2148916235":
                    JOptionPane.showMessageDialog(null, "This account is from a country where Xbox Live is not available");
                    microsoftAuthenticate.launcherFrame.showError("Login failed");
                    break;
                case "2148916236":
                case "2148916237":
                    JOptionPane.showMessageDialog(null, "This account needs to complete the age verification process");
                    microsoftAuthenticate.launcherFrame.showError("Login failed");
                    break;
                case "2148916238":
                    JOptionPane.showMessageDialog(null, "This account is not eligible to play on Xbox Live");
                    microsoftAuthenticate.launcherFrame.showError("Login failed");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    microsoftAuthenticate.launcherFrame.showError(e.getMessage());
                    break;
            }
        }
    }

    private void getMinecraftAccessToken(String uhs, String xstsToken) {
        try {
            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("identityToken", "XBL3.0 x=" + uhs + "; " + xstsToken);

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.apiMinecraftAuthUrl, "POST", jsonParameters.toString());
            String access_token = tokenResponse.getString("access_token");
            this.microsoftAuthenticate.getMinecraftStore(access_token);
            this.microsoftAuthenticate.getMinecraftProfile(access_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
