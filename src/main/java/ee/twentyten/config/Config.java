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
  private String username;
  private String password;
  private Boolean passwordSaved;
  private String clientToken;
  private String accessToken;
  private String profileId;
  private String profileName;
  private Boolean usingBeta;
  private Boolean usingAlpha;
  private Boolean usingInfdev;
  private String selectedVersion;

  public static Config load() {
    Config config = null;
    File configFile = null;
    try {
      configFile = new File(LauncherManager.getWorkingDirectory(), "twentyten.properties");
      if (!configFile.exists()) {
        boolean created = configFile.createNewFile();
        if (!created) {
          throw new IOException("Failed to create config file");
        }
      }

      try (FileInputStream fis = new FileInputStream(configFile)) {
        CustomLinkedProperties properties = new CustomLinkedProperties();
        properties.load(fis);

        config = new Config();
        config.username = properties.getProperty("username");
        config.password = CipherManager.decryptValue(properties.getProperty("password"));
        config.passwordSaved = Boolean.parseBoolean(properties.getProperty("password-saved"));
        config.clientToken = properties.getProperty("client-token");
        config.accessToken = CipherManager.decryptValue(properties.getProperty("access-token"));
        config.profileId = properties.getProperty("profile-id");
        config.profileName = properties.getProperty("profile-name");
        config.usingBeta = Boolean.parseBoolean(properties.getProperty("using-beta"));
        config.usingAlpha = Boolean.parseBoolean(properties.getProperty("using-alpha"));
        config.usingInfdev = Boolean.parseBoolean(properties.getProperty("using-infdev"));
        config.selectedVersion = properties.getProperty("selected-version");
      } catch (IOException ioe2) {
        DebugLoggingManager.logError(Config.class, "Failed to load config file", ioe2);
      }
    } catch (IOException ioe1) {
      DebugLoggingManager.logError(Config.class, "Failed to get working directory", ioe1);
    }
    DebugLoggingManager.logInfo(Config.class,
        String.format("\"%s\"", configFile.getAbsolutePath()));
    return config;
  }

  public void save() {
    CustomLinkedProperties general = new CustomLinkedProperties();
    general.setProperty("username", this.username != null ? this.username : "");
    general.setProperty("password",
        CipherManager.encryptValue(this.password != null ? this.password : null));
    general.setProperty("password-saved", String.valueOf(this.passwordSaved));

    CustomLinkedProperties profile = new CustomLinkedProperties();
    profile.setProperty("client-token", this.clientToken != null ? this.clientToken : "");
    profile.setProperty("access-token",
        CipherManager.encryptValue(this.accessToken != null ? this.accessToken : ""));
    profile.setProperty("profile-id", this.profileId != null ? this.profileId : "");
    profile.setProperty("profile-name", this.profileName != null ? this.profileName : "");

    CustomLinkedProperties options = new CustomLinkedProperties();
    options.setProperty("using-beta", String.valueOf(this.usingBeta));
    options.setProperty("using-alpha", String.valueOf(this.usingAlpha));
    options.setProperty("using-infdev", String.valueOf(this.usingInfdev));
    options.setProperty("selected-version", this.selectedVersion);

    File configFile = new File(LauncherManager.getWorkingDirectory(), "twentyten.properties");
    try (FileOutputStream fos = new FileOutputStream(configFile.getAbsolutePath())) {
      general.store(fos, "General");
      profile.store(fos, "Profile");
      options.store(fos, "Options");
    } catch (IOException ioe) {
      DebugLoggingManager.logError(this.getClass(), "Failed to save config file", ioe);
    }

    DebugLoggingManager.logInfo(Config.class,
        String.format("\"%s\"", configFile.getAbsolutePath()));
  }
}
