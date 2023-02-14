package ee.twentyten.ui.options;

import ee.twentyten.lang.LauncherLanguage;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
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
  private JTabbedPane tabbedPane;
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

    int orientation = JTabbedPane.TOP;

    Border border = BorderFactory.createEmptyBorder(
        8, 8, 8, 8);
    this.setBorder(border);

    BorderLayout borderLayout = new BorderLayout(
        0, 8);
    FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
    this.optionsVersionPanel = new OptionsVersionPanel(
        borderLayout, true
    );
    this.optionsLanguagePanel = new OptionsLanguagePanel(
        borderLayout, true
    );

    this.tabbedPane.add(
        this.versionTabText, this.optionsVersionPanel
    );
    this.tabbedPane.add(
        this.languageTabText, this.optionsLanguagePanel
    );

    this.createTopTabbedPane(orientation);
    this.createBottomPanel(flowLayout);
  }

  private void createTopTabbedPane(
      int orientation
  ) {
    this.tabbedPane = new JTabbedPane(orientation);
    this.add(this.tabbedPane, BorderLayout.NORTH);
  }

  private void createBottomPanel(
      LayoutManager layout
  ) {
    int tabbedPaneWidth = this.tabbedPane.getPreferredSize().width;

    JPanel bottomPanel = new JPanel(
        layout, true
    );
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.cancelButton = new JButton(this.cancelButtonText);
    this.openGameDirectoryButton = new JButton(
        this.openGameDirectoryButtonText);
    this.saveOptionsButton = new JButton(this.saveOptionsButtonText);

    bottomPanel.add(this.cancelButton);
    bottomPanel.add(Box.createHorizontalStrut(tabbedPaneWidth >> 2));
    bottomPanel.add(this.openGameDirectoryButton);
    bottomPanel.add(this.saveOptionsButton);
  }
}
