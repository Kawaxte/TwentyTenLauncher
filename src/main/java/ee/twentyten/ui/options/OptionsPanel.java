package ee.twentyten.ui.options;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.log.ELevel;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import ee.twentyten.util.VersionUtils;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class OptionsPanel extends JPanel implements ActionListener {

  @Getter
  @Setter
  private static OptionsPanel instance;
  private final JButton openGameDirectoryButton;
  private final JButton saveOptionsButton;
  @Getter
  private final LanguageOptionsGroupBox languageGroupBox;
  @Getter
  private final VersionOptionsGroupBox versionGroupBox;

  {
    this.languageGroupBox = new LanguageOptionsGroupBox();
    this.versionGroupBox = new VersionOptionsGroupBox();

    this.openGameDirectoryButton = new JButton("op.button.openGameDirectoryButton");
    this.saveOptionsButton = new JButton("op.button.saveOptionsButton");

    this.openGameDirectoryButton.addActionListener(this);
    this.saveOptionsButton.addActionListener(this);
  }

  public OptionsPanel() {
    super(new GridBagLayout(), true);

    OptionsPanel.setInstance(this);
    this.buildPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.openGameDirectoryButton,
        "op.button.openGameDirectoryButton");
    LanguageUtils.setTextToComponent(bundle, this.saveOptionsButton, "op.button.saveOptionsButton");
  }

  private void buildPanel() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    this.add(this.languageGroupBox, gbc);

    gbc.gridy = 1;
    this.add(this.versionGroupBox, gbc);

    gbc.gridy = 2;
    this.add(this.buildBottomPanel(), gbc);
  }

  private JPanel buildBottomPanel() {
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT), true);
    bottomPanel.add(this.openGameDirectoryButton, 0);
    bottomPanel.add(this.saveOptionsButton, 1);
    return bottomPanel;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.openGameDirectoryButton) {
      boolean isDesktopSupported = Desktop.isDesktopSupported();
      if (isDesktopSupported) {
        Desktop desktop = Desktop.getDesktop();
        boolean isSupported = desktop.isSupported(Action.OPEN);
        if (isSupported) {
          try {
            Desktop.getDesktop().open(LauncherUtils.workingDirectory);
          } catch (IOException ioe) {
            LoggerUtils.log("Failed to open Minecraft directory", ioe, ELevel.ERROR);
          }
        }
      }
    }
    if (source == this.saveOptionsButton) {
      LanguageUtils.updateSelectedLanguage(LanguageOptionsGroupBox.getInstance());
      VersionUtils.updateSelectedVersion(VersionOptionsGroupBox.getInstance());
    }
  }
}
