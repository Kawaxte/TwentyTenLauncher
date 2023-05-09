package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.custom.JGroupBox;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class LanguageGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  public static LanguageGroupBox instance;
  private final JLabel setLanguageLabel;
  @Getter private final JComboBox<String> languageComboBox;

  {
    this.setLanguageLabel = new JLabel("lgb.setLanguageLabel", SwingConstants.RIGHT);
    this.languageComboBox = new JComboBox<>();
  }

  public LanguageGroupBox() {
    super("lgb.title", true);

    LanguageGroupBox.instance = this;
    this.setLayout(this.getGroupLayout());

    this.languageComboBox.addActionListener(this);

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle((String) selectedLanguage)
            : LauncherLanguage.bundle);

    LauncherLanguageUtils.updateLanguageComboBox(this);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(bundle, this.setLanguageLabel, "lgb.setLanguageLabel");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.setLanguageLabel)
            .addComponent(this.languageComboBox));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.setLanguageLabel)
                    .addComponent(this.languageComboBox)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.languageComboBox)) {
      val selectedLanguageEqual =
          Objects.equals(
              LauncherConfig.lookup.get("selectedLanguage"),
              this.languageComboBox.getSelectedItem());
      OptionsPanel.instance.getSaveOptionsButton().setEnabled(!selectedLanguageEqual);
    }
  }
}
