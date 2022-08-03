package net.minecraft.auth.microsoft;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

public final class MFormData {
    private MFormData() {
        throw new UnsupportedOperationException();
    }

    public static String ofFormData(Map<Object, Object> parameter) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : parameter.entrySet()) {
            Arrays.asList(Arrays.asList(URLEncoder.encode(entry.getKey().toString(), "UTF-8"), "="),
                    Arrays.asList(URLEncoder.encode(entry.getValue().toString(), "UTF-8"), "&")).forEach(strings ->
                    strings.forEach(sb::append));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}