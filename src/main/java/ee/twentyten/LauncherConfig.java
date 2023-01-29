package ee.twentyten;

import ee.twentyten.core.LinkedProperties;
import ee.twentyten.util.CipherManager;
import ee.twentyten.util.DirectoryManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LauncherConfig {

  public static LauncherConfig config;
  private String clientToken;
  private String accessToken;
  private String username;
  private String password;
  private Boolean rememberBox;
  private Boolean betaBox;
  private Boolean alphaBox;
  private Boolean infdevBox;
  private String versionId;

  public static LauncherConfig load() {
    File configFile = new File(DirectoryManager.getWorkingDirectory(), "twentyten.properties");
    if (!configFile.exists()) {
      boolean created;
      try {
        created = configFile.createNewFile();
        if (!created) {
          throw new IOException("Failed to create launcher config file");
        }
      } catch (IOException e) {
        throw new RuntimeException("Can't create launcher config file", e);
      }
    }

    try (FileInputStream fis = new FileInputStream(configFile)) {
      LinkedProperties properties = new LinkedProperties();
      properties.load(fis);

      LauncherConfig config = new LauncherConfig();
      config.clientToken = properties.getProperty("client-token");
      config.accessToken = CipherManager.decrypt(properties.getProperty("access-token"));
      config.username = properties.getProperty("username");
      config.password = CipherManager.decrypt(properties.getProperty("password"));
      config.rememberBox = Boolean.parseBoolean(properties.getProperty("password-saved"));
      config.betaBox = Boolean.parseBoolean(properties.getProperty("using-beta"));
      config.alphaBox = Boolean.parseBoolean(properties.getProperty("using-alpha"));
      config.infdevBox = Boolean.parseBoolean(properties.getProperty("using-infdev"));
      config.versionId = properties.getProperty("selected-version");
      return config;
    } catch (IOException e) {
      throw new RuntimeException("Can't load launcher config file", e);
    }
  }

  public void save() {
    LinkedProperties properties = new LinkedProperties();
    properties.setProperty("client-token", this.clientToken != null ? this.clientToken : null);
    properties.setProperty("access-token",
        CipherManager.encrypt(this.accessToken != null ? this.accessToken : null));
    properties.setProperty("username", this.username != null ? this.username : null);
    properties.setProperty("password",
        CipherManager.encrypt(this.password != null ? this.password : null));
    properties.setProperty("password-saved", String.valueOf(this.rememberBox));
    properties.setProperty("using-beta", String.valueOf(this.betaBox));
    properties.setProperty("using-alpha", String.valueOf(this.alphaBox));
    properties.setProperty("using-infdev", String.valueOf(this.infdevBox));
    properties.setProperty("selected-version", this.versionId);

    File configFile = new File(DirectoryManager.getWorkingDirectory(), "twentyten.properties");
    try (FileOutputStream fos = new FileOutputStream(configFile.getAbsolutePath())) {
      properties.store(fos, "TwentyTen Launcher Properties File");
    } catch (IOException e) {
      throw new RuntimeException("Can't save launcher config file", e);
    }
  }
}
