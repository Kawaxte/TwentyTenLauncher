package net.minecraft.launcher.auth;

public class AuthCredentials {
    public static AuthCredentials credentials = null;
    private final String accessToken;
    private final String uuid;

    public AuthCredentials(String accessToken, String uuid) {
        credentials = this;
        this.accessToken = accessToken;
        this.uuid = uuid;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getUuid() {
        return this.uuid;
    }
}
