package ch.kawaxte.launcher.util;

import ch.kawaxte.launcher.minecraft.EState;
import ch.kawaxte.launcher.ui.LauncherNoNetworkPanel;
import ch.kawaxte.launcher.ui.MicrosoftAuthPanel;
import ch.kawaxte.launcher.ui.MinecraftAppletWrapper;
import ch.kawaxte.launcher.ui.YggdrasilAuthPanel;
import ch.kawaxte.launcher.ui.options.LanguageGroupBox;
import ch.kawaxte.launcher.ui.options.OptionsDialog;
import ch.kawaxte.launcher.ui.options.OptionsPanel;
import ch.kawaxte.launcher.ui.options.VersionGroupBox;

/**
 * Utility class for retrieving keys from the language resource bundles.
 *
 * <p>Note that this class is a singleton, and thus cannot be instantiated directly.
 *
 * @author Kawaxte
 * @since 1.5.0623_01
 */
public final class LauncherLanguageUtils {

  private LauncherLanguageUtils() {}

  /**
   * Returns an array of keys for {@link EState}
   *
   * @return an array of keys from the language resource bundle
   * @see EState
   */
  public static String[] getESEnumKeys() {
    return new String[] {
      "es_enum.initialise",
      "es_enum.checkCache",
      "es_enum.downloadPackages",
      "es_enum.extractPackages",
      "es_enum.updateClasspath",
      "es_enum.done"
    };
  }

  /**
   * Returns an array of keys for {@link LanguageGroupBox}
   *
   * @return an array of keys from the language resource bundle
   * @see LanguageGroupBox
   */
  public static String[] getLGBKeys() {
    return new String[] {"lgb.title", "lgb.setLanguageLabel"};
  }

  /**
   * Returns an array of keys for {@link LauncherNoNetworkPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see LauncherNoNetworkPanel
   */
  public static String[] getLNPPKeys() {
    return new String[] {
      "lnnp.errorLabel.signin",
      "lnnp.errorLabel.signin_null",
      "lnnp.errorLabel.signin_outdated",
      "lnnp.errorLabel.signin_2148916233",
      "lnnp.errorLabel.signin_2148916238",
      "lnnp.playOnlineLabel",
      "lnnp.playOfflineButton",
      "lnnp.retryButton"
    };
  }

  /**
   * Returns an array of keys for {@link MinecraftAppletWrapper}
   *
   * @return an array of keys from the language resource bundle
   * @see MinecraftAppletWrapper
   */
  public static String[] getGAWKeys() {
    return new String[] {
      "maw.updaterStarted",
      "maw.updaterErrored",
      "maw.taskStateMessage.error",
      "maw.taskProgressMessage"
    };
  }

  /**
   * Returns an array of keys for {@link MicrosoftAuthPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see MicrosoftAuthPanel
   */
  public static String[] getMAPKeys() {
    return new String[] {
      "map.enterCodeInBrowserLabel", "map.openInBrowserButton", "map.cancelButton"
    };
  }

  /**
   * Returns an array of keys for {@link OptionsDialog}
   *
   * @return an array of keys from the language resource bundle
   * @see OptionsDialog
   */
  public static String[] getODKeys() {
    return new String[] {"od.title"};
  }

  /**
   * Returns an array of keys for {@link OptionsPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see OptionsPanel
   */
  public static String[] getOPKeys() {
    return new String[] {
      "op.versionGroupBox", "op.languageGroupBox", "op.openFolderButton", "op.saveOptionsButton"
    };
  }

  /**
   * Returns an array of keys for {@link VersionGroupBox}
   *
   * @return an array of keys from the language resource bundle
   * @see VersionGroupBox
   */
  public static String[] getVGBKeys() {
    return new String[] {"vgb.title", "vgb.showVersionsCheckBox", "vgb.useVersionLabel"};
  }

  /**
   * Returns an array of keys for {@link YggdrasilAuthPanel}
   *
   * @return an array of keys from the language resource bundle
   * @see YggdrasilAuthPanel
   */
  public static String[] getYAPKeys() {
    return new String[] {
      "yap.microsoftSigninButton",
      "yap.microsoftSigninButton.signing_in",
      "yap.usernameLabel",
      "yap.passwordLabel",
      "yap.optionsButton",
      "yap.rememberPasswordCheckBox",
      "yap.linkLabel",
      "yap.linkLabel.outdated",
      "yap.signinButton",
      "yap.signinButton.signing_in"
    };
  }
}
