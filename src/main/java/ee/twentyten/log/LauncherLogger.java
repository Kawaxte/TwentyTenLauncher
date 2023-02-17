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

public class LauncherLogger {

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
    this.logFileName = String.format("twentyten_%s.log", this.getTimestamp());
  }

  private String getTimestamp() {
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    return String.format("%04d-%02d-%02dT%02d-%02d-%02d", year, month, day,
        hour, minute, second);
  }

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
        throw new UnsupportedOperationException(String.valueOf(platform));
    }

    try {
      String gpuIdKey = "ee.twentyten.hw.gpu.id";
      String cpuIdKey = "ee.twentyten.hw.cpu.id";
      this.cpuId = this.getHardwareId(platform, cpuIdCommand, cpuIdKey);
      this.gpuId = this.getHardwareId(platform, gpuIdCommand, gpuIdKey);
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to retrieve the hardware ID", ioe, false);
    }
  }

  private String getHardwareId(EPlatform platform, String command, String key)
      throws IOException {
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
        result = result.substring(result.indexOf("\r") + 1);
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
    result = result.trim();

    System.setProperty(key, result);
    return result;
  }

  private boolean isSystemMessageWritten(File target) {

    /* BufferedReader and FileReader are used because Scanner is not thread-safe */
    try (BufferedReader br = new BufferedReader(new FileReader(target))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("[SYSTEM]")) {
          return false;
        }
      }
    } catch (IOException ioe) {
      LoggerHelper.logError(
          "Failed to check if system message has been written", ioe, false);
    }
    return true;
  }

  private void writeSystemMessage(FileWriter fw) throws IOException {
    String systemMessage = "[SYSTEM] %s: %s%n";

    String[] systemMessages = {
        String.format(systemMessage, "ee.twentyten.version",
            LauncherFrame.launcherVersion),
        String.format(systemMessage, "ee.twentyten.hw.cpu.id", this.cpuId),
        String.format(systemMessage, "ee.twentyten.hw.gpu.id", this.gpuId),
        String.format(systemMessage, "os.name", EPlatform.OS_NAME),
        String.format(systemMessage, "os.version", this.osVersion),
        String.format(systemMessage, "os.arch", this.osArch),
        String.format(systemMessage, "java.runtime.name", this.javaRuntimeName),
        String.format(systemMessage, "java.runtime.version",
            this.javaRuntimeVersion),
        String.format(systemMessage, "java.vm.name", this.javaVmName),
        String.format(systemMessage, "java.vm.version", this.javaVmVersion)};
    for (String line : systemMessages) {
      fw.write(line);
    }
  }

  public void writeLog(String message) {
    File logDirectory = FileHelper.createDirectory(FileHelper.workingDirectory,
        "logs");
    File logFile = new File(logDirectory, this.logFileName);
    if (!logFile.exists()) {
      try {
        boolean isNewFileCreated = logFile.createNewFile();
        if (!isNewFileCreated) {
          LoggerHelper.logError("Failed to create log file", false);
          return;
        }
      } catch (IOException ioe) {
        LoggerHelper.logError("Failed to create log file", ioe, false);
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to write to log file", ioe, false);
    }
  }

  public void writeLog(String message, Throwable t) {
    File logDirectory = FileHelper.createDirectory(FileHelper.workingDirectory,
        "logs");
    File logFile = new File(logDirectory, this.logFileName);
    if (!logFile.exists()) {
      try {
        boolean isNewFileCreated = logFile.createNewFile();
        if (!isNewFileCreated) {
          LoggerHelper.logError("Failed to create log file", false);
          return;
        }
      } catch (IOException ioe) {
        LoggerHelper.logError("Failed to create log file", ioe, false);
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));

      try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(
          sw)) {
        t.printStackTrace(pw);

        fw.write(String.format("%s", sw));
      }
    } catch (IOException ioe) {
      LoggerHelper.logError("Failed to write to log file", ioe, false);
    }
  }
}
