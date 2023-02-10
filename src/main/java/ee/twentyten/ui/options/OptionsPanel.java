package ee.twentyten.ui.options;

import ee.twentyten.util.LanguageHelper;
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
  private final String versionTabTitle;
  private final String languageTabTitle;
  private final String cancelButtonText;
  private final String openGameDirectoryButtonText;
  private final String saveOptionsButtonText;
  private JTabbedPane tabbedPane;
  private OptionsVersionPanel optionsVersionPanel;
  private OptionsLanguagePanel optionsLanguagePanel;
  private JButton cancelButton;
  private JButton openGameDirectoryButton;
  private JButton saveOptionsButton;

  {
    this.versionTabTitle = LanguageHelper.getString("ovp.string.title.text");
    this.languageTabTitle = LanguageHelper.getString("olp.string.title.text");
    this.cancelButtonText = LanguageHelper.getString("op.button.cancelButton.text");
    this.openGameDirectoryButtonText = LanguageHelper.getString(
        "op.button.openGameDirectoryButton.text");
    this.saveOptionsButtonText = LanguageHelper.getString("op.button.saveOptionsButton.text");
  }

  public OptionsPanel() {
    super(new BorderLayout(), true);

    this.initComponents();
  }

  private void initComponents() {
    this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    this.add(this.tabbedPane, BorderLayout.NORTH);

    this.optionsVersionPanel = new OptionsVersionPanel();
    this.tabbedPane.add(this.versionTabTitle, this.optionsVersionPanel);

    this.optionsLanguagePanel = new OptionsLanguagePanel();
    this.tabbedPane.add(this.languageTabTitle, this.optionsLanguagePanel);

    this.createBottomPanel();
  }

  private void createBottomPanel() {
    int tabbedPaneWidth = this.tabbedPane.getPreferredSize().width;

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.cancelButton = new JButton(this.cancelButtonText);
    bottomPanel.add(this.cancelButton);
    bottomPanel.add(Box.createHorizontalStrut(tabbedPaneWidth >> 2));

    this.openGameDirectoryButton = new JButton(this.openGameDirectoryButtonText);
    bottomPanel.add(this.openGameDirectoryButton);

    this.saveOptionsButton = new JButton(this.saveOptionsButtonText);
    bottomPanel.add(this.saveOptionsButton);
  }
}
