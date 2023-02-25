package ee.twentyten.config;

import lombok.Getter;

@Getter
abstract class LauncherConfig {

  public abstract void load();

  public abstract void save();

  public abstract String encrypt(String value);

  public abstract String decrypt(String value);
}
