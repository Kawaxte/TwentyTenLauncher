package net.minecraft.launcher.auth.microsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

public final class MSFormData {
    private MSFormData() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static String encodeFormData(Map<Object, Object> parameter) {
        StringBuilder sb = new StringBuilder();
        parameter.forEach((key, value) -> {
            try {
                Arrays.asList(Arrays.asList(URLEncoder.encode(key.toString(), "UTF-8"), "="),
                        Arrays.asList(URLEncoder.encode(value.toString(), "UTF-8"), "&"))
                        .forEach(strings -> strings.forEach(sb::append));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to encode data from " + key + " to " + value, e);
            }
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
