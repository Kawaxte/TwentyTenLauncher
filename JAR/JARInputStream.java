package JAR;

import java.io.InputStream;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class JARInputStream {
    public InputStream getJARInputStream(URLConnection connection) throws Exception {
        final InputStream[] is = new InputStream[1];
        AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
            is[0] = connection.getInputStream();
            return null;
        });
        if (is[0] == null) {
            throw new Exception("Failed to open " + connection.getURL().toString());
        }
        return is[0];
    }
}