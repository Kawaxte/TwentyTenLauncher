package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public final class RuntimeHelper {

  public static final long MIN_MEMORY;
  public static final long MAX_MEMORY;

  static {
    MIN_MEMORY = 524288L;
    MAX_MEMORY = Runtime.getRuntime().maxMemory();
  }

  private RuntimeHelper() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static String getProcessOutput(Process process) {
    try (InputStreamReader isr = new InputStreamReader(
        process.getInputStream()); BufferedReader br = new BufferedReader(isr)) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append(System.lineSeparator());
      }
      return sb.toString();
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to read process output", ioe, true);
    }
    return null;
  }

  public static Process execute(String command) throws IOException {
    Process process = Runtime.getRuntime().exec(command);
    try {
      process.waitFor();
    } catch (InterruptedException ie) {
      LoggerHelper.logError("Failed to wait for process to terminate", ie, true);
    }
    return process;
  }

  public static void execute(String command, String s) throws IOException {
    Runtime.getRuntime().exec(String.format("%s %s", command, s));
  }

  public static void executeUrl(EPlatform platform, String url) throws IOException {
    Objects.requireNonNull(platform, "platform == null!");
    switch (platform) {
      case MACOSX:
        RuntimeHelper.execute("open", url);
        break;
      case LINUX:
        RuntimeHelper.execute("xdg-open", url);
        break;
      case WINDOWS:
        RuntimeHelper.execute("rundll32 url.dll,FileProtocolHandler", url);
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
  }

  public static void executeFile(EPlatform platform, File f) throws IOException {
    Objects.requireNonNull(platform, "platform == null!");
    switch (platform) {
      case MACOSX:
        RuntimeHelper.execute("open", f.getAbsolutePath());
        break;
      case LINUX:
        RuntimeHelper.execute("xdg-open", f.getAbsolutePath());
        break;
      case WINDOWS:
        RuntimeHelper.execute("explorer.exe", f.getAbsolutePath());
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
  }
}
