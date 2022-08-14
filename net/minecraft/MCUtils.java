/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.swing.UIManager;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class MCUtils {
    public static String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    public static String userHome = System.getProperty("user.home", ".");
    public static String applicationData = System.getenv("APPDATA");

    private MCUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public enum OS {
        osx, linux, windows
    }

    public static JSONObject requestMethod(String url, String method, String data) throws IOException {
        String result;
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            String type = data.contains("{") && data.contains("}") ? "application/json" : "application/x-www-form-urlencoded";

            HttpResponse response;
            switch (method) {
                case "GET":
                    HttpGet get = new HttpGet(url);
                    if (!"application/json".equals(type)) {
                        get.addHeader("Authorization", "Bearer " + data);
                    }
                    get.setHeader("Content-Type", type);
                    response = client.execute(get);
                    switch (response.getStatusLine().getStatusCode()) {
                        case 200:
                            System.out.println(response.getStatusLine().getStatusCode() + " OK");
                            result = EntityUtils.toString(response.getEntity());
                            break;
                        case 401:
                            System.err.println(response.getStatusLine().getStatusCode() + " Unauthorised");
                        default:
                            throw new IOException(String.valueOf(response.getStatusLine().getStatusCode()));
                    }
                    break;
                case "POST":
                    HttpPost post = new HttpPost(url);
                    post.setHeader("Content-Type", type);
                    post.setEntity(new StringEntity(data));
                    response = client.execute(post);
                    switch (response.getStatusLine().getStatusCode()) {
                        case 200:
                            System.out.println(response.getStatusLine().getStatusCode() + " OK");
                            result = EntityUtils.toString(response.getEntity());
                            break;
                        case 401:
                            System.err.println(response.getStatusLine().getStatusCode() + " Unauthorised");
                        default:
                            throw new IOException(String.valueOf(response.getStatusLine().getStatusCode()));
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown method: " + method);
            }
        }
        return new JSONObject(result);
    }

    public static boolean isJavaFXInstalled() {
        try {
            Class.forName("javafx.application.Application");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public static OS getPlatform() {
        if (osName.contains("mac")) {
            return OS.osx;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OS.linux;
        } else if (osName.contains("win")) {
            return OS.windows;
        } else {
            throw new RuntimeException("Unknown OS: " + osName);
        }
    }

    public static File getWorkingDirectory() {
        File workingDirectory;
        switch (getPlatform()) {
            case osx:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/");
                break;
            case linux:
                workingDirectory = new File(userHome, ".minecraft");
                break;
            case windows:
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, ".minecraft/");
                    break;
                }
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, ".minecraft/");
        }

        if (!workingDirectory.exists() && !workingDirectory.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        return workingDirectory;
    }

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
