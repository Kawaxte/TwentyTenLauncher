package net.minecraft.auth.microsoft;

import net.minecraft.auth.AUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MTokens {
    private final MAuthenticate minecraftAuthenticate;

    public MTokens(MAuthenticate minecraftAuthenticate) {
        this.minecraftAuthenticate = minecraftAuthenticate;
    }

    void acquireAccessToken(String code) {
        try {
            HashMap<Object, Object> parameters = new HashMap<>();
            parameters.put("client_id", "00000000402b5328");
            parameters.put("code", code);
            parameters.put("grant_type", "authorization_code");
            parameters.put("redirect_uri", MAuthenticate.redirectUrl);
            parameters.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

            JSONObject jsonResponse = AUtils.requestPOST(MAuthenticate.tokenUrl, MFormData.ofFormData(parameters));
            acquireXBLToken(jsonResponse.getString("access_token"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    void acquireXBLToken(String accessToken) {
        try {
            JSONObject propertyParameters = new JSONObject();
            propertyParameters.put("AuthMethod", "RPS").put("SiteName", "user.auth.xboxlive.com").put("RpsTicket", accessToken);

            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("Properties", propertyParameters).put("RelyingParty", "http://auth.xboxlive.com").put("TokenType", "JWT");

            JSONObject jsonResponse = AUtils.requestJSONPOST(MAuthenticate.xboxAuthUrl, String.valueOf(jsonParameters));
            acquireXSTSToken(jsonResponse.getString("Token"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    void acquireXSTSToken(String token) {
        try {
            JSONObject propertyParameters = new JSONObject();
            propertyParameters.put("SandboxId", "RETAIL").put("UserTokens", new String[]{token});

            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("Properties", propertyParameters).put("RelyingParty", "rp://api.minecraftservices.com/").put("TokenType", "JWT");

            JSONObject jsonResponse = AUtils.requestJSONPOST(MAuthenticate.xstsAuthUrl, String.valueOf(jsonParameters));
            acquireMCToken(jsonResponse.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs"),
                    jsonResponse.getString("Token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void acquireMCToken(String uhs, String token) {
        try {
            JSONObject jsonParameters = new JSONObject();
            jsonParameters.put("identityToken", "XBL3.0 x=" + uhs + "; " + token);

            JSONObject jsonResponse = AUtils.requestJSONPOST(MAuthenticate.minecraftLoginUrl, String.valueOf(jsonParameters));
            minecraftAuthenticate.acquireMCProfile(jsonResponse.getString("access_token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}