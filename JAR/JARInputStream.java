package JAR;

import java.io.InputStream;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class JarInputStream {
    public InputStream getJarInputStream(URLConnection connection) throws Exception {
        final InputStream[] is = new InputStream[1];
        AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () -> {
            is[0] = connection.getInputStream();
            return null;
        });
        assert is[0] != null : "Failed to open " + connection.getURL().toString();
        return is[0];
    }
}