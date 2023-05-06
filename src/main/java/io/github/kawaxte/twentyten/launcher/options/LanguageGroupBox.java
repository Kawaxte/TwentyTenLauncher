package io.github.kawaxte.twentyten.launcher.options;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import io.github.kawaxte.twentyten.ui.JGroupBox;
import java.awt.LayoutManager;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class LanguageGroupBox extends JGroupBox {

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

    val selectedLanguage = CONFIG.getSelectedLanguage();
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : LANGUAGE.getBundle());

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
}
