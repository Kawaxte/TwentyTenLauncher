package ee.twentyten.log;

import ee.twentyten.EPlatform;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.RuntimeHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Objects;

public class LauncherLogger {

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
    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    int minute = Calendar.getInstance().get(Calendar.MINUTE);
    int second = Calendar.getInstance().get(Calendar.SECOND);

    String formattedYear = String.format("%04d", year);
    String formattedMonth = String.format("%02d", month);
    String formattedDay = String.format("%02d", day);
    String formattedHour = String.format("%02d", hour);
    String formattedMinute = String.format("%02d", minute);
    String formattedSecond = String.format("%02d", second);
    return String.format("%s-%s-%sT%s-%s-%s", formattedYear, formattedMonth, formattedDay,
        formattedHour, formattedMinute, formattedSecond);
  }

  private void getHardwareId() {
    EPlatform platform = EPlatform.getPlatform();
    Objects.requireNonNull(platform, "platform == null!");

    String processorIdKey = "ee.twentyten.hw.cpu.id";
    String graphicsIdKey = "ee.twentyten.hw.gpu.id";
    String processorCommand;
    String graphicsCommand;

    switch (platform) {
      case MACOSX:
        processorCommand = "sysctl -n machdep.cpu.brand_string";
        graphicsCommand = "system_profiler SPDisplaysDataType | grep -i chipset";
        break;
      case LINUX:
        processorCommand = "lscpu | grep -i name";
        graphicsCommand = "lspci | grep -i vga";
        break;
      case WINDOWS:
        processorCommand = "wmic cpu get name";
        graphicsCommand = "wmic path win32_VideoController get name";
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }

    try {
      this.cpuId = getHardwareId(platform, processorCommand, processorIdKey);
      this.gpuId = getHardwareId(platform, graphicsCommand, graphicsIdKey);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private String getHardwareId(EPlatform platform, String command, String key) throws IOException {
    Process process = RuntimeHelper.execute(command);

    String result = RuntimeHelper.getProcessOutput(process);
    Objects.requireNonNull(result, "result == null!");

    switch (platform) {
      case MACOSX:
      case LINUX:
        result = result.substring(result.indexOf(":") + 1);
        break;
      case WINDOWS:
        result = result.substring(result.indexOf("\r\n") + 2);
        break;
      default:
        throw new UnsupportedOperationException(String.valueOf(platform));
    }
    result = result.trim();

    System.setProperty(key, result);
    return result;
  }

  private boolean isSystemMessageWritten(File f) {
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("[SYSTEM]")) {
          return false;
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return true;
  }

  private void writeSystemMessage(FileWriter fw) throws IOException {
    String systemMessage = "[SYSTEM] %s: %s%n";
    String[] systemMessages = {
        String.format(systemMessage, "ee.twentyten.version", LauncherFrame.version),
        String.format(systemMessage, "ee.twentyten.hw.cpu.id", this.cpuId),
        String.format(systemMessage, "ee.twentyten.hw.gpu.id", this.gpuId),
        String.format(systemMessage, "os.name", EPlatform.OS_NAME),
        String.format(systemMessage, "os.version", this.osVersion),
        String.format(systemMessage, "os.arch", this.osArch),
        String.format(systemMessage, "java.runtime.name", this.javaRuntimeName),
        String.format(systemMessage, "java.runtime.version", this.javaRuntimeVersion),
        String.format(systemMessage, "java.vm.name", this.javaVmName),
        String.format(systemMessage, "java.vm.version", this.javaVmVersion)};
    for (String line : systemMessages) {
      fw.write(line);
    }
  }

  public void writeLog(String message) {
    File logDirectory = FileHelper.createDirectory(FileHelper.workingDirectory, "logs");
    File logFile = new File(logDirectory, this.logFileName);
    if (!logFile.exists()) {
      try {
        boolean created = logFile.createNewFile();
        if (!created) {
          throw new IOException("Failed to create log file");
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeLog(String message, Throwable t) {
    File logDirectory = FileHelper.createDirectory(FileHelper.workingDirectory, "logs");
    File logFile = new File(logDirectory, this.logFileName);
    if (!logFile.exists()) {
      try {
        boolean created = logFile.createNewFile();
        if (!created) {
          throw new IOException("Failed to create log file");
        }
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }

    try (FileWriter fw = new FileWriter(logFile, true)) {
      if (this.isSystemMessageWritten(logFile)) {
        this.writeSystemMessage(fw);
      }
      fw.write(String.format("%s%n", message));

      try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
        if (t == null) {
          t = new NullPointerException("t == null!");
        }
        t.printStackTrace(pw);
        fw.write(String.format("%s", sw));
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
