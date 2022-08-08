package net.minecraft.launcher.auth;

public class AuthCredentials {
    protected static String username;
    protected static String clientToken;
    protected static String accessToken;
    protected static String uuid;

    private AuthCredentials(String username, String clientToken, String accessToken, String uuid) {
        AuthCredentials.username = username;
        AuthCredentials.clientToken = clientToken;
        AuthCredentials.accessToken = accessToken;
        AuthCredentials.uuid = uuid;
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    protected static String setClientToken(String clientToken) {
        AuthCredentials.clientToken = clientToken;
        return clientToken;
    }

    protected static String setAccessToken(String accessToken) {
        AuthCredentials.accessToken = accessToken;
        return accessToken;
    }

    protected static String setUuid(String uuid) {
        AuthCredentials.uuid = uuid;
        return uuid;
    }
}
