package net.minecraft.launcher.auth.microsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

public class MSFormData {
    private MSFormData() {
        throw new UnsupportedOperationException();
    }

    protected static String encodeFormData(Map<Object, Object> parameter) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : parameter.entrySet()) {
            try {
                Arrays.asList(Arrays.asList(URLEncoder.encode(entry.getKey().toString(), "UTF-8"), "="),
                        Arrays.asList(URLEncoder.encode(entry.getValue().toString(), "UTF-8"), "&")).forEach(strings ->
                        strings.forEach(sb::append));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to encode data from " + entry.getKey() + " to " + entry.getValue(), e);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
