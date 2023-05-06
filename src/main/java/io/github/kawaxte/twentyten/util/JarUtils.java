package io.github.kawaxte.twentyten.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.jar.JarFile;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JarUtils {

  static final Logger LOGGER;

  static {
    LOGGER = LogManager.getLogger(JarUtils.class);
  }

  private JarUtils() {}

  public static String getManifestAttribute(String name) {
    val jarFileUrl =
        Optional.ofNullable(LauncherUtils.class.getProtectionDomain().getCodeSource().getLocation())
            .orElseThrow(() -> new RuntimeException("Failed to get code source location"));
    try (val jarFile = new JarFile(new File(jarFileUrl.toURI()))) {
      val manifest = jarFile.getManifest();
      val attributes = manifest.getMainAttributes();
      return attributes.getValue(name);
    } catch (IOException ioe) {
      LOGGER.error("Failed to read manifest from {}", jarFileUrl, ioe);
    } catch (URISyntaxException urise) {
      LOGGER.error("Failed to parse {} as URI", jarFileUrl, urise);
    } finally {
      if (jarFileUrl.getFile().endsWith(".jar")) {
        LOGGER.info("Retrieve '{}' from {}", name, jarFileUrl);
      }
    }
    return null;
  }
}
