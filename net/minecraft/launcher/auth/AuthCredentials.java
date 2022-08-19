package net.minecraft.launcher.auth;

public class AuthCredentials {
    public static AuthCredentials credentials = null;
    private final String accessToken;

    public AuthCredentials(String accessToken) {
        credentials = this;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
