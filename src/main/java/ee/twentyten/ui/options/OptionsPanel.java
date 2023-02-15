package ee.twentyten.ui.options;

import ee.twentyten.lang.LauncherLanguage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import lombok.Getter;

@Getter
public class OptionsPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final String versionTabText;
  private final String languageTabText;
  private final String cancelButtonText;
  private final String openGameDirectoryButtonText;
  private final String saveOptionsButtonText;
  private final OptionsVersionPanel optionsVersionPanel;
  private final OptionsLanguagePanel optionsLanguagePanel;
  private final JTabbedPane tabbedPane;
  private JButton cancelButton;
  private JButton openGameDirectoryButton;
  private JButton saveOptionsButton;

  {
    this.versionTabText = LauncherLanguage
        .getString("ovp.string.title");
    this.languageTabText = LauncherLanguage
        .getString("olp.string.title");
    this.cancelButtonText = LauncherLanguage
        .getString("op.button.cancelButton");
    this.openGameDirectoryButtonText = LauncherLanguage
        .getString("op.button.openGameDirectoryButton");
    this.saveOptionsButtonText = LauncherLanguage
        .getString("op.button.saveOptionsButton");
  }

  public OptionsPanel() {
    super(new BorderLayout(), true);

    this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    this.add(this.tabbedPane, BorderLayout.NORTH);

    this.optionsVersionPanel = new OptionsVersionPanel();
    this.tabbedPane.add(this.versionTabText, this.optionsVersionPanel);

    this.optionsLanguagePanel = new OptionsLanguagePanel();
    this.tabbedPane.add(this.versionTabText, this.optionsLanguagePanel);

    this.createBottomPanel();
  }

  private void createBottomPanel() {
    int tabbedPaneWidth = this.tabbedPane.getPreferredSize().width;

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.cancelButton = new JButton(this.cancelButtonText);
    bottomPanel.add(this.cancelButton);
    bottomPanel.add(Box.createHorizontalStrut(tabbedPaneWidth >> 2));

    this.openGameDirectoryButton = new JButton(
        this.openGameDirectoryButtonText);
    bottomPanel.add(this.openGameDirectoryButton);

    this.saveOptionsButton = new JButton(this.saveOptionsButtonText);
    bottomPanel.add(this.saveOptionsButton);
  }
}
