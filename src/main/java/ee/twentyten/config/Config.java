package ee.twentyten.config;

import ee.twentyten.custom.CustomLinkedProperties;
import ee.twentyten.util.CipherManager;
import ee.twentyten.util.DebugLoggingManager;
import ee.twentyten.util.LauncherManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {

  public static Config instance;
  private String clientToken;
  private String accessToken;
  private String username;
  private String password;
  private Boolean passwordSaved;
  private Boolean usingBeta;
  private Boolean usingAlpha;
  private Boolean usingInfdev;
  private String selectedVersion;

  public static Config load() {
    Config c = null;
    try {
      File configFile = new File(LauncherManager.getWorkingDirectory(), "twentyten.properties");
      if (!configFile.exists()) {
        boolean created = configFile.createNewFile();
        if (!created) {
          throw new IOException("Failed to create config file");
        }
      }

      try (FileInputStream fis = new FileInputStream(configFile)) {
        CustomLinkedProperties properties = new CustomLinkedProperties();
        properties.load(fis);

        c = new Config();
        c.clientToken = properties.getProperty("client-token");
        c.accessToken = CipherManager.decryptValue(properties.getProperty("access-token"));
        c.username = properties.getProperty("username");
        c.password = CipherManager.decryptValue(properties.getProperty("password"));
        c.passwordSaved = Boolean.parseBoolean(properties.getProperty("password-saved"));
        c.usingBeta = Boolean.parseBoolean(properties.getProperty("using-beta"));
        c.usingAlpha = Boolean.parseBoolean(properties.getProperty("using-alpha"));
        c.usingInfdev = Boolean.parseBoolean(properties.getProperty("using-infdev"));
        c.selectedVersion = properties.getProperty("selected-version");

        DebugLoggingManager.logInfo(Config.class,
            String.format("\"%s\"", configFile.getAbsolutePath()));
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(Config.class, "Failed to load config file", ioe2);
      }
    } catch (IOException ioe3) {
      DebugLoggingManager.logError(Config.class, "Failed to get working directory", ioe3);
    }
    return c;
  }

  public void save() {
    CustomLinkedProperties properties = new CustomLinkedProperties();
    properties.setProperty("client-token", this.clientToken != null ? this.clientToken : null);
    properties.setProperty("access-token",
        CipherManager.encryptValue(this.accessToken != null ? this.accessToken : null));
    properties.setProperty("username", this.username != null ? this.username : null);
    properties.setProperty("password",
        CipherManager.encryptValue(this.password != null ? this.password : null));
    properties.setProperty("password-saved", String.valueOf(this.passwordSaved));
    properties.setProperty("using-beta", String.valueOf(this.usingBeta));
    properties.setProperty("using-alpha", String.valueOf(this.usingAlpha));
    properties.setProperty("using-infdev", String.valueOf(this.usingInfdev));
    properties.setProperty("selected-version", this.selectedVersion);

    try {
      File configFile = new File(LauncherManager.getWorkingDirectory(), "twentyten.properties");
      try (FileOutputStream fos = new FileOutputStream(configFile.getAbsolutePath())) {
        properties.store(fos, "TwentyTen Launcher Properties File");

        DebugLoggingManager.logInfo(Config.class,
            String.format("\"%s\"", configFile.getAbsolutePath()));
      } catch (IOException ioe1) {
        DebugLoggingManager.logError(this.getClass(), "Failed to save config file", ioe1);
      }
    } catch (IOException ioe2) {
      DebugLoggingManager.logError(this.getClass(), "Failed to get working directory", ioe2);
    }
  }
}
