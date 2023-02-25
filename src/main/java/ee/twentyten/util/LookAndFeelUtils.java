package ee.twentyten.util;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import ee.twentyten.log.ELogger;
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
      LoggerUtils.log(UIManager.getLookAndFeel().getClass().getCanonicalName(), ELogger.INFO);
    } catch (UnsupportedLookAndFeelException ulafe) {
      LoggerUtils.log("Failed to set look and feel", ulafe, ELogger.ERROR);
    } catch (ClassNotFoundException cnfe) {
      LoggerUtils.log("Failed to find look and feel class", cnfe, ELogger.ERROR);
    } catch (InstantiationException ie) {
      LoggerUtils.log("Failed to instantiate look and feel class", ie, ELogger.ERROR);
    } catch (IllegalAccessException iae) {
      LoggerUtils.log("Failed to access look and feel class", iae, ELogger.ERROR);
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
      LoggerUtils.log("Failed to read registry key", w32e, ELogger.ERROR);
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
      LoggerUtils.log("Failed to read registry key", w32e, ELogger.ERROR);
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

        String classicThemePath = String.format(
            "%s\\Resources\\Ease of Access Themes\\classic.theme", SystemRoot);
        String classicThemeContents = FileUtils.readFileContents(classicThemePath);
        String[] classicThemeLines = classicThemeContents.split("\\r?\\n");
        return Objects.equals(currentThemeLines[3], classicThemeLines[3]);
      }
    } catch (Win32Exception w32e) {
      LoggerUtils.log("Failed to read registry key", w32e, ELogger.ERROR);
    }
    return false;
  }
}
