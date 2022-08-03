package net.minecraft.auth;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public final class AUtils {
    private AUtils() {
        throw new UnsupportedOperationException();
    }

    public static JSONObject requestPOST(String url, String parameters) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity(parameters));

        HttpResponse response = client.execute(post);
        return new JSONObject(response != null ? EntityUtils.toString(response.getEntity()) : "");
    }

    public static JSONObject requestJSONPOST(String url, String jsonParameters) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonParameters));

        HttpResponse response = client.execute(post);
        return new JSONObject(response != null ? EntityUtils.toString(response.getEntity()) : "");
    }

    public static JSONObject requestJSONGET(String url, String accessToken) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + accessToken);

            HttpResponse response = client.execute(get);
            return new JSONObject(response != null ? EntityUtils.toString(response.getEntity()) : "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}