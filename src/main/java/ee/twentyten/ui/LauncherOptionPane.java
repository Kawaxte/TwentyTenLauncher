package ee.twentyten.ui;

import ee.twentyten.lang.LauncherLanguage;
import javax.swing.JOptionPane;

public class LauncherOptionPane extends JOptionPane {

  private static final long serialVersionUID = 1L;
  public static LauncherOptionPane instance;
  private final String optionPaneTitleErrorText;
  private final String optionPaneVersionErrorText;

  {
    this.optionPaneTitleErrorText = LauncherLanguage
        .getString("lp.string.title.error");
    this.optionPaneVersionErrorText = LauncherLanguage
        .getString("lp.string.error.version");
  }

  public void showVersionError() {
    JOptionPane.showMessageDialog(
        LauncherFrame.instance,
        this.optionPaneVersionErrorText,
        this.optionPaneTitleErrorText,
        JOptionPane.ERROR_MESSAGE
    );
  }
}
