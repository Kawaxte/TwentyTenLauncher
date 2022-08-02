package net.minecraft.launcher;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class LUtils {
    public static JSONObject excuteJSONGet(String url) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet(url);
            get.setHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(get);
            return new JSONObject(response != null ? EntityUtils.toString(response.getEntity()) : "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}