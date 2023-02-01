package net.minecraft;

import ee.twentyten.config.Config;
import ee.twentyten.launcher.EPlatform;
import ee.twentyten.util.DebugLoggingManager;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import javax.swing.JApplet;
import lombok.Getter;

@Getter
public class MinecraftUpdater implements Runnable {

  private static final String LWJGL_JAR_URL;
  private static final String LWJGL_NATIVE_URL;
  private static final String CLIENT_JAR_URL;

  static {
    LWJGL_JAR_URL = "https://archive.org/download/lwjgl-2/lwjgl-2.9.3/jar/%s";
    LWJGL_NATIVE_URL = "https://archive.org/download/lwjgl-2/lwjgl-2.9.3/native/%s/%s";
    CLIENT_JAR_URL = "https://archive.org/download/legacy-minecraft-%s/%s";
  }

  protected final String subtaskMessage;
  private final ClassLoader loader;
  private final URL[] urls;
  protected boolean errorOccurred;
  protected String errorMessage;
  private int state;
  private int percentage;

  public MinecraftUpdater() {
    this.errorOccurred = false;
    this.state = 0;
    this.percentage = 0;
    this.errorMessage = "";
    this.subtaskMessage = "";
    this.loader = null;
    this.urls = null;
  }

  private String getState() {
    switch (this.state) {
      case 1:
        return "Initializing loader";
      case 2:
        return "Determining packages to load";
      case 3:
        return "Checking cache for existing files";
      case 4:
        return "Downloading packages";
      case 5:
        return "Moving downloaded packages";
      case 6:
        return "Updating classpath";
      case 7:
        return "Done loading";
      default:
        return "Unknown state";
    }
  }

  private void determinePackages() {
    this.state = 2;
    this.percentage = 5;

    boolean isUsingBeta = Config.instance.getUsingBeta();
    boolean isUsingAlpha = Config.instance.getUsingAlpha();
    boolean isUsingInfdev = Config.instance.getUsingInfdev();

    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    //TODO: DETERMINE PACKAGES
  }

  private void checkCache() {
    this.state = 3;
    this.percentage = 10;

    //TODO: CHECK IF WE HAVE THE FILES IN CACHE (this means we don't have to download them)
  }

  private void downloadPackages(File dest) {
    this.state = 4;
    this.percentage = 15;

    //TODO: DOWNLOAD THE FILES
  }

  private void movePackages() {
    this.state = 5;
    //TODO: FIND THE FINAL PERCENTAGE VALUE FROM DOWNLOAD METHOD AND SET IT HERE
    this.percentage = 0;

    //TODO: MOVE DIFFERENT FILES TO THEIR RESPECTIVE LOCATIONS
  }

  private void updateClasspath() {
    this.state = 6;
    this.percentage = 95;

    //TODO: UPDATE THE CLASSPATH
  }

  protected JApplet createAppletInstance() {
    Objects.requireNonNull(this.loader, "loader == null!");
    try {
      Class<?> clazz = this.loader.loadClass("net.minecraft.client.MinecraftApplet");
      return (JApplet) clazz.newInstance();
    } catch (ReflectiveOperationException roe) {
      this.showError("Failed to create applet instance", roe);
    }
    return null;
  }

  private void showError(String message, Throwable t) {
    this.errorOccurred = true;
    this.errorMessage = String.format("Fatal error occurred (%s): %s", this.state,
        t.getMessage());

    DebugLoggingManager.logError(this.getClass(), message, t);
  }

  @Override
  public void run() {
    this.state = 1;
    this.percentage = 0;
  }
}
