package net.minecraft.launcher.auth.microsoft;

import net.minecraft.MCUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MSTokens {
    private final MSAuthenticate microsoftAuthenticate;

    public MSTokens(MSAuthenticate microsoftAuthenticate) {
        this.microsoftAuthenticate = microsoftAuthenticate;
    }

    public void acquireAccessToken(String authCode) {
        try {
            HashMap<Object, Object> parameters = new HashMap<>();
            parameters.put("client_id", "00000000402b5328");
            parameters.put("code", authCode);
            parameters.put("grant_type", "authorization_code");
            parameters.put("redirect_uri", MSAuthenticate.loaDesktopUrl);
            parameters.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.loaTokenUrl, "POST", MSFormData.encodeFormData(parameters));
            if (tokenResponse == null) {
                throw new IOException("Failed to acquire access token");
            }
            String access_token = tokenResponse.getString("access_token");
            acquireXBLToken(access_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireXBLToken(String access_token) {
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
            if (tokenResponse == null) {
                throw new IOException("Failed to acquire XBL token");
            }
            acquireXSTSToken(tokenResponse.getString("Token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireXSTSToken(String token) {
        try {
            JSONObject propertyParameters = new JSONObject();
            propertyParameters.put("SandboxId", "RETAIL");
            propertyParameters.put("UserTokens", new String[]{token});

            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("Properties", propertyParameters);
            jsonParameters.put("RelyingParty", "rp://api.minecraftservices.com/");
            jsonParameters.put("TokenType", "JWT");

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.xblXstsAuthurl, "POST", jsonParameters.toString());
            if (tokenResponse == null) {
                throw new IOException("Failed to acquire XSTS token");
            }
            String uhs = tokenResponse.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            String xstsToken = tokenResponse.getString("Token");
            acquireMCToken(uhs, xstsToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireMCToken(String uhs, String xstsToken) {
        try {
            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("identityToken", "XBL3.0 x=" + uhs + "; " + xstsToken);

            JSONObject tokenResponse = MCUtils.requestMethod(MSAuthenticate.apiMinecraftAuthUrl, "POST", jsonParameters.toString());
            if (tokenResponse == null) {
                throw new IOException("Failed to acquire MC token");
            }
            String access_token = tokenResponse.getString("access_token");
            this.microsoftAuthenticate.acquireMCProfile(access_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
