package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class RuntimeHelper {

  private RuntimeHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void executeCommand(String command, String path) throws IOException {
    Runtime.getRuntime().exec(String.format("%s %s", command, path));
  }

  public static void executeCommand(EPlatform platform, String url) throws IOException {
    Objects.requireNonNull(platform, "platform == null!");
    switch (platform) {
      case MACOSX:
        executeCommand("open", url);
        break;
      case LINUX:
        executeCommand("xdg-open", url);
        break;
      case WINDOWS:
        executeCommand("rundll32 url.dll,FileProtocolHandler", url);
        break;
      default:
        throw new UnsupportedOperationException("Can't execute string command");
    }
  }

  public static void executeCommand(EPlatform platform, File f) throws IOException {
    Objects.requireNonNull(platform, "platform == null!");
    switch (platform) {
      case MACOSX:
        executeCommand("open", f.getAbsolutePath());
        break;
      case LINUX:
        executeCommand("xdg-open", f.getAbsolutePath());
        break;
      case WINDOWS:
        executeCommand("explorer.exe", f.getAbsolutePath());
        break;
      default:
        throw new UnsupportedOperationException("Can't execute string command");
    }
  }
}
