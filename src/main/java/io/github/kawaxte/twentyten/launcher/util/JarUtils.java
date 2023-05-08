package io.github.kawaxte.twentyten.launcher.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.jar.JarFile;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JarUtils {

  private static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(JarUtils.class);
  }

  private JarUtils() {}

  public static String getManifestAttribute(String key) {
    val jarFileUrl =
        Optional.ofNullable(LauncherUtils.class.getProtectionDomain().getCodeSource().getLocation())
            .orElseThrow(() -> new NullPointerException("jarFileUrl must not be null"));
    try (val jarFile = new JarFile(new File(jarFileUrl.toURI()))) {
      val manifest = jarFile.getManifest();
      val attributes = manifest.getMainAttributes();
      return attributes.getValue(key);
    } catch (FileNotFoundException fnfe) {
      return "9999.999999.999.9";
    } catch (IOException ioe) {
      LOGGER.error("Failed to retrieve '{}' from {}", key, jarFileUrl, ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Failed to parse {} as URI", jarFileUrl, urise);
    } finally {
      if (jarFileUrl.getFile().endsWith(".jar")) {
        LOGGER.info("Retrieved '{}' from {}", key, jarFileUrl);
      }
    }
    return null;
  }
}
