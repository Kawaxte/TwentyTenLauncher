package ee.twentyten.log;

import ee.twentyten.EPlatform;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.RuntimeHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import lombok.Getter;

public class LauncherLogger {

  @Getter
  public static LauncherLogger instance;
  private final String logFileName;
  private final String javaRuntimeName;
  private final String javaRuntimeVersion;
  private final String javaVmName;
  private final String javaVmVersion;
  private final String osVersion;
  private final String osArch;
  private String cpuId;
  private String gpuId;

  {
    this.javaRuntimeName = System.getProperty("java.runtime.name");
    this.javaRuntimeVersion = System.getProperty("java.runtime.version");
    this.javaVmName = System.getProperty("java.vm.name");
    this.javaVmVersion = System.getProperty("java.vm.version");

    this.osVersion = System.getProperty("os.version");
    this.osArch = System.getProperty("os.arch");

    this.getHardwareId();
    this.cpuId = System.getProperty("ee.twentyten.hw.cpu.id");
    this.gpuId = System.getProperty("ee.twentyten.hw.gpu.id");
  }

  public LauncherLogger() {
    this.logFileName = String.format(
        "twentyten_%s.log",
        this.getTimestamp()
    );
  }

  /**
   * Returns a string representation of the current date and time in the format
   * of "yyyy-MM-dd'T'HH-mm-ss".
   *
   * @return the current date and time in a formatted string
   */
  private String getTimestamp() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);

    return String.format(
        "%04d-%02d-%02dT%02d-%02d-%02d",
        year, month, day, hour, minute, second
    );
  }

  /**
   * Retrieves the hardware ID of the CPU and GPU for the current platform.
   * <p>
   * The hardware ID is retrieved through the execution of platform-specific
   * commands.
   *
   * @throws UnsupportedOperationException if the platform is not supported
   */
  private void getHardwareId() {
    EPlatform platform = EPlatform.getPlatform();

    String cpuIdCommand = "";
    String gpuIdCommand = "";
    switch (platform) {
      case MACOSX:
        cpuIdCommand = "sysctl -n machdep.cpu.brand_string";
        gpuIdCommand = "system_profiler SPDisplaysDataType | grep -i chipset";
        break;
      case LINUX:
        cpuIdCommand = "lscpu | grep -i name";
        gpuIdCommand = "lspci | grep -i vga";
        break;
      case WINDOWS:
        cpuIdCommand = "wmic cpu get name";
        gpuIdCommand = "wmic path win32_VideoController get name";
        break;
      default:
        String unsupportedPlatform = String.format(
            "Unsupported platform: %s",
            platform
        );

        Throwable uoe = new UnsupportedOperationException(unsupportedPlatform);

        LoggerHelper.logError(uoe.getMessage(), uoe, false);
        break;
    }

    try {
      String gpuIdKey = "ee.twentyten.hw.gpu.id";
      String cpuIdKey = "ee.twentyten.hw.cpu.id";
      this.cpuId = this.getHardwareId(
          platform,
          cpuIdCommand,
          cpuIdKey
      );
      this.gpuId = this.getHardwareId(
          platform,
          gpuIdCommand,
          gpuIdKey
      );
    } catch (IOException ioe) {
      String errorMessage = String.format(
          "Failed to retrieve the hardware ID: %s",
          ioe.getMessage()
      );
      
      LoggerHelper.logError(errorMessage, ioe, false);
    }
  }

  /**
   * Retrieves the hardware ID for a specific platform by executing a command
   * and parsing the result.
   *
   * @param platform the platform for which to retrieve the hardware ID
   * @param command  the command to execute to retrieve the hardware ID
   * @param key      the key to use to store the hardware ID in the system
   *                 properties
   * @return the hardware ID for the specified platform
   * @throws IOException                   if there is an error retrieving the
   *                                       hardware ID
   * @throws UnsupportedOperationException if the platform is not supported
   */
  private String getHardwareId(
      EPlatform platform,
      String command,
      String key
  ) throws IOException {
    Process process = RuntimeHelper.executeCommand(command);
    String result = RuntimeHelper.getOutput(process);
    if (result == null) {
      return null;
    }

    switch (platform) {
      case MACOSX:
      case LINUX:
        result = result.substring(result.indexOf(":") + 1);
        break;
      case WINDOWS:
        result = result.substring(result.indexOf("\r\n") + 2);
        break;
      default:
        String unsupportedPlatform = String.format(
            "Unsupported platform: %s",
            platform
        );

        Throwable uoe = new UnsupportedOperationException(unsupportedPlatform);

        LoggerHelper.logError(uoe.getMessage(), uoe, false);
        break;
    }
    result = result.trim();

    System.setProperty(key, result);
    return result;
  }

  /**
   * Checks if a system message has already been written to the specified file.
   *
   * @param file the file to check for a system message
   * @return true if a system message has not been written, false otherwise
   */
  private boolean isSystemMessageWritten(
      File file
  ) {

    /*
     * BufferedReader and FileReader are used instead of Scanner because
     * Scanner is not thread-safe. */
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("[SYSTEM]")) {
          return false;
        }
      }
    } catch (IOException ioe) {
      String errorMessage = String.format(
          "Failed to check if system message has been written: %s",
          ioe.getMessage()
      );

      LoggerHelper.logError(errorMessage, ioe, false);
    }

    return true;
  }

  /**
   * Writes a system message to the specified file writer.
   *
   * @param fw the file writer to use for writing the system message
   * @throws IOException if an I/O error occurs while writing to the file
   *                     writer
   */
  private void writeSystemMessage(
      FileWriter fw
  ) throws IOException {
    String systemMessage = "[SYSTEM] %s: %s%n";

    String[] systemMessages = {
        String.format(
            systemMessage,
            "ee.twentyten.version", LauncherFrame.launcherVersion),
        String.format(
            systemMessage,
            "ee.twentyten.hw.cpu.id", this.cpuId),
        String.format(
            systemMessage,
            "ee.twentyten.hw.gpu.id", this.gpuId),
        String.format(
            systemMessage,
            "os.name", EPlatform.OS_NAME),
        String.format(
            systemMessage,
            "os.version", this.osVersion),
        String.format(
            systemMessage,
            "os.arch", this.osArch),
        String.format(
            systemMessage,
            "java.runtime.name", this.javaRuntimeName),
        String.format(
            systemMessage, "java.runtime.version", this.javaRuntimeVersion),
        String.format(
            systemMessage,
            "java.vm.name", this.javaVmName),
        String.format(
            systemMessage,
            "java.vm.version", this.javaVmVersion)
    };

    for (String line : systemMessages) {
      fw.write(line);
    }
  }

  /**
   * Writes a log message to a log file in the logs directory.
   *
   * @param message the message to be written to the log file
   */
  public void writeLog(
      String message
  ) {
    File logDirectory = FileHelper.createDirectory(
        FileHelper.workingDirectory, "logs"
    );
    File logFile = new File(
        logDirectory, this.logFileName
    );
    if (!logFile.exists()) {
      try {
        boolean isNewFileCreated = logFile.createNewFile();
        if (!isNewFileCreated) {
          String errorMessage = String.format(
              "Failed to create log file: %s",
              logFile.getAbsolutePath()
          );

          Throwable ioe = new IOException(errorMessage);

          LoggerHelper.logError(errorMessage, ioe, false);
          return;
        }
      } catch (IOException ioe) {
        String errorMessage = String.format(
            "Failed to create log file: %s",
            logFile.getAbsolutePath()
        );

        LoggerHelper.logError(errorMessage, ioe, false);
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));
    } catch (IOException ioe) {
      String errorMessage = String.format(
          "Failed to write to log file: %s",
          logFile.getAbsolutePath()
      );

      LoggerHelper.logError(errorMessage, ioe, false);
    }
  }

  /**
   * Writes a log message to a log file with an optional throwable stack trace.
   *
   * @param message the message to be written to the log file
   * @param t       optional throwable stack trace to write to the log
   */
  public void writeLog(
      String message,
      Throwable t
  ) {
    File logDirectory = FileHelper.createDirectory(
        FileHelper.workingDirectory, "logs"
    );
    File logFile = new File(
        logDirectory, this.logFileName
    );
    if (!logFile.exists()) {
      try {
        boolean isNewFileCreated = logFile.createNewFile();
        if (!isNewFileCreated) {
          String errorMessage = String.format(
              "Failed to create log file: %s",
              logFile.getAbsolutePath()
          );

          Throwable ioe = new IOException(errorMessage);

          LoggerHelper.logError(errorMessage, ioe, false);
          return;
        }
      } catch (IOException ioe) {
        String errorMessage = String.format(
            "Failed to create log file: %s",
            logFile.getAbsolutePath()
        );

        LoggerHelper.logError(errorMessage, ioe, false);
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));

      try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(
          sw)) {
        if (t == null) {
          String errorMessage = String.format(
              "Failed to write to log file: %s",
              logFile.getAbsolutePath()
          );

          Throwable npe = new NullPointerException(errorMessage);

          LoggerHelper.logError(errorMessage, npe, false);
          return;
        }

        t.printStackTrace(pw);
        fw.write(String.format("%s", sw));
      }
    } catch (IOException ioe) {
      String errorMessage = String.format(
          "Failed to write to log file: %s",
          logFile.getAbsolutePath()
      );

      LoggerHelper.logError(errorMessage, ioe, false);
    }
  }
}
