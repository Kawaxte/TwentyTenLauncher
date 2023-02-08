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
  private final String cpuId;
  private final String gpuId;

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
    try {
      String processor;
      String graphics;

      Process process;
      switch (platform) {
        case MACOSX:
          process = RuntimeHelper.execute("sysctl -n machdep.cpu.brand_string");
          processor = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(processor, "processor == null!");
          processor = processor.trim();
          System.setProperty("ee.twentyten.hw.cpu.id", processor);

          process = RuntimeHelper.execute("system_profiler SPDisplaysDataType | grep -i chipset");
          graphics = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(graphics, "graphics == null!");
          graphics = graphics.substring(graphics.indexOf(":") + 1).trim();
          System.setProperty("ee.twentyten.hw.gpu.id", graphics);
          break;
        case LINUX:
          process = RuntimeHelper.execute("lscpu | grep -i name");
          processor = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(processor, "processor == null!");
          processor = processor.substring(processor.indexOf(":") + 1).trim();
          System.setProperty("ee.twentyten.hw.cpu.id", processor);

          process = RuntimeHelper.execute("lspci | grep -i vga");
          graphics = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(graphics, "graphics == null!");
          graphics = graphics.substring(graphics.indexOf(":") + 1).trim();
          System.setProperty("ee.twentyten.hw.gpu.id", graphics);
          break;
        case WINDOWS:
          process = RuntimeHelper.execute("wmic cpu get name");
          processor = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(processor, "processor == null!");
          processor = processor.substring(processor.indexOf(System.lineSeparator()) + 1).trim();
          System.setProperty("ee.twentyten.hw.cpu.id", processor);

          process = RuntimeHelper.execute("wmic path win32_VideoController get name");
          graphics = RuntimeHelper.getProcessOutput(process);
          Objects.requireNonNull(graphics, "graphics == null!");
          graphics = graphics.substring(graphics.indexOf(System.lineSeparator()) + 1).trim();
          System.setProperty("ee.twentyten.hw.gpu.id", graphics);
          break;
        default:
          throw new UnsupportedOperationException(String.valueOf(platform));
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
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
        writeSystemMessage(fw);
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
}
