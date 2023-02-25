package ee.twentyten.ui;

import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LanguageUtils;
import javax.swing.JOptionPane;
import lombok.Getter;

public class LauncherOptionPane extends JOptionPane {

  private static final long serialVersionUID = 1L;
  public static LauncherOptionPane instance;

  static {
    LauncherOptionPane.instance = new LauncherOptionPane();
  }

  @Getter
  private final boolean isNoShowVersionsSelected;

  {
    this.isNoShowVersionsSelected = !ConfigUtils.config.isShowBetaVersionsSelected()
        && !ConfigUtils.config.isShowAlphaVersionsSelected()
        && !ConfigUtils.config.isShowInfdevVersionsSelected();
  }

  private LauncherOptionPane() {
    super();
  }

  public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(LauncherFrame.instance, message,
        LanguageUtils.optionPaneTitleErrorText, JOptionPane.ERROR_MESSAGE);
  }
}
