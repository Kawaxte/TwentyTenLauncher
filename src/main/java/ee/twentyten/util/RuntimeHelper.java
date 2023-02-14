package ee.twentyten.util;

import ee.twentyten.EPlatform;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
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

  public static String getOutput(
      Process process
  ) {
    try (InputStreamReader isr = new InputStreamReader(
        process.getInputStream());
        BufferedReader br = new BufferedReader(isr)) {

      /* Using 'StringBuilder' to avoid creating a new 'String' object for
       * every line. */
      StringBuilder sb = new StringBuilder();

      /* Read the output of the process line by line. */
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append(System.lineSeparator());
      }
      return sb.toString();
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to read process output",
          ioe, true
      );
    }
    return null;
  }

  public static void openBrowser(
      String url
  ) {
    try {
      Desktop.getDesktop().browse(URI.create(url));
    } catch (IOException ioe1) {
      LoggerHelper.logError(
          "Failed to launch default browser",
          ioe1, true
      );

      EPlatform platform = EPlatform.getPlatform();
      Objects.requireNonNull(
          platform, "platform == null!"
      );

      /* Launch the default browser for the current platform because the
       * default browser for the current desktop environment failed to
       * launch. */
      try {
        RuntimeHelper.executeCommand(platform, url);
      } catch (IOException ioe2) {
        LoggerHelper.logError(
            "Failed to execute string command",
            ioe2, true
        );
      }
    }
  }

  public static Process executeCommand(
      String command
  ) throws IOException {

    /* Execute the command in a separate process.*/
    Process process = Runtime.getRuntime().exec(command);
    try {

      /* Unless we want to let the process run in the background, we need to
       * wait for it to finish. */
      process.waitFor();
    } catch (InterruptedException ie) {
      LoggerHelper.logError(
          "Failed to interrupt current thread",
          ie, true
      );
    }
    return process;
  }

  public static void executeCommand(
      String command,
      String s
  ) throws IOException {

    /* Execute the command in a separate process.*/
    Process process = Runtime.getRuntime()
        .exec(String.format(
            "%s %s",
            command, s)
        );
    try {

      /* Unless we want to let the process run in the background, we need to
       * wait for it to finish. */
      process.waitFor();
    } catch (InterruptedException ie) {
      LoggerHelper.logError(
          "Failed to wait for process to finish",
          ie, true
      );
    }
  }

  public static void executeCommand(
      EPlatform platform,
      String url
  ) throws IOException {
    Objects.requireNonNull(
        platform, "platform == null!"
    );

    switch (platform) {
      case MACOSX:
        RuntimeHelper.executeCommand(
            "open", url
        );
        break;
      case LINUX:
        RuntimeHelper.executeCommand(
            "xdg-open", url
        );
        break;
      case WINDOWS:
        RuntimeHelper.executeCommand(
            "rundll32 url.dll,FileProtocolHandler", url
        );
        break;
      default:
        Throwable uoe = new UnsupportedOperationException(String.format(
            "Unsupported platform: %s",
            platform)
        );

        LoggerHelper.logError(
            uoe.getMessage(),
            uoe, true
        );
    }
  }

  public static void executeCommand(
      EPlatform platform,
      File src
  ) throws IOException {
    Objects.requireNonNull(platform, "platform == null!");

    switch (platform) {
      case MACOSX:
        RuntimeHelper.executeCommand(
            "open", src.getAbsolutePath()
        );
        break;
      case LINUX:
        RuntimeHelper.executeCommand(
            "xdg-open", src.getAbsolutePath()
        );
        break;
      case WINDOWS:
        RuntimeHelper.executeCommand(
            "explorer.exe", src.getAbsolutePath()
        );
        break;
      default:
        Throwable uoe = new UnsupportedOperationException(String.format(
            "Unsupported platform: %s",
            platform)
        );

        LoggerHelper.logError(
            uoe.getMessage(),
            uoe, true
        );
    }
  }
}
