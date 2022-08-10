package net.minecraft.launcher.auth;

public class AuthCredentials {
    public static AuthCredentials credentials;
    private final String username;
    private final String accessToken;
    private final String uuid;

    public AuthCredentials(String username, String accessToken, String uuid) {
        credentials = this;
        this.username = username;
        this.accessToken = accessToken;
        this.uuid = uuid;
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getUuid() {
        return this.uuid;
    }
}
