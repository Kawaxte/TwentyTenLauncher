package ee.twentyten.util.launcher.ui;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.FileUtils;
import ee.twentyten.util.SystemUtils;
import ee.twentyten.util.log.LoggerUtils;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class LookAndFeelUtils {

  public static boolean isUsingWindowsClassicTheme;

  private LookAndFeelUtils() {
    throw new UnsupportedOperationException("Can't instantiate utility class");
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      LoggerUtils.logMessage(UIManager.getLookAndFeel().getClass().getCanonicalName(), ELevel.INFO);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerUtils.logMessage("Can't set look and feel", ulafe, ELevel.ERROR);
    } catch (ClassNotFoundException cnfe) {
      LoggerUtils.logMessage("Failed to find look and feel class", cnfe, ELevel.ERROR);
    } catch (InstantiationException ie) {
      LoggerUtils.logMessage("Failed to instantiate look and feel class", ie, ELevel.ERROR);
    } catch (IllegalAccessException iae) {
      LoggerUtils.logMessage("Failed to access look and feel class", iae, ELevel.ERROR);
    }
  }

  public static boolean isWindowsClassic() {
    double parsedOsVersion = Double.parseDouble(SystemUtils.osVersion);

    /* Windows XP and Windows Server 2003 */
    if (parsedOsVersion == 5.1 || parsedOsVersion == 5.2) {
      LookAndFeelUtils.isUsingWindowsClassicTheme = LookAndFeelUtils.isWindowsClassicOnWhistler();
    }

    /* Windows Vista and Windows Server 2008 */
    if (parsedOsVersion == 6.0) {
      LookAndFeelUtils.isUsingWindowsClassicTheme = LookAndFeelUtils.isWindowsClassicOnLonghorn();
    }

    /* Windows 7 and Windows Server 2008 R2 */
    if (parsedOsVersion == 6.1) {
      LookAndFeelUtils.isUsingWindowsClassicTheme = LookAndFeelUtils.isWindowsClassicOnSeven();
    }
    return LookAndFeelUtils.isUsingWindowsClassicTheme;
  }

  private static boolean isWindowsClassicOnWhistler() {
    try {
      Map<String, Object> keyValues = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER,
          "Software\\Microsoft\\Windows\\CurrentVersion\\ThemeManager");
      if (!keyValues.isEmpty()) {
        boolean hasThemeActiveKey = keyValues.containsKey("ThemeActive");
        boolean hasWCreatedUserKey = keyValues.containsKey("WCreatedUser");
        if (hasThemeActiveKey && hasWCreatedUserKey) {
          return keyValues.get("ThemeActive").equals("0") && keyValues.get("WCreatedUser")
              .equals("1");
        }
      }
    } catch (Win32Exception w32e) {
      LoggerUtils.logMessage("Failed to read registry key", w32e, ELevel.ERROR);
    }
    return false;
  }

  private static boolean isWindowsClassicOnLonghorn() {
    try {
      Map<String, Object> keyValues = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER,
          "Software\\Microsoft\\Windows\\CurrentVersion\\ThemeManager");
      if (!keyValues.isEmpty()) {
        boolean hasThemeActiveKey = keyValues.containsKey("ThemeActive");
        boolean hasLMVersionKey = keyValues.containsKey("LMVersion");
        if (hasThemeActiveKey && hasLMVersionKey) {
          return keyValues.get("ThemeActive").equals("0") && keyValues.get("LMVersion")
              .equals("105");
        }
        return keyValues.get("ThemeActive").equals("0");
      }
    } catch (Win32Exception w32e) {
      LoggerUtils.logMessage("Failed to read registry key", w32e, ELevel.ERROR);
    }
    return false;
  }

  private static boolean isWindowsClassicOnSeven() {
    try {
      TreeMap<String, Object> keyValues = Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER,
          "Software\\Microsoft\\Windows\\CurrentVersion\\Themes");

      String SystemRoot = System.getenv("SystemRoot");
      String currentThemePath = (String) keyValues.get("CurrentTheme");
      if (!currentThemePath.isEmpty()) {
        String currentThemeContents = FileUtils.readFileContents(currentThemePath);
        String[] currentThemeLines = currentThemeContents.split("\\r?\\n");

        String classicThemePath = MessageFormat.format(
            "{0}\\Resources\\Ease of Access Themes\\classic.theme", SystemRoot);
        String classicThemeContents = FileUtils.readFileContents(classicThemePath);
        String[] classicThemeLines = classicThemeContents.split("\\r?\\n");
        return Objects.equals(currentThemeLines[3], classicThemeLines[3]);
      }
    } catch (Win32Exception w32e) {
      LoggerUtils.logMessage("Failed to read registry key", w32e, ELevel.ERROR);
    }
    return false;
  }
}
