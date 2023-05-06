package io.github.kawaxte.twentyten.launcher.options;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherVersionUtils;
import io.github.kawaxte.twentyten.ui.JGroupBox;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class VersionGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  public static VersionGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JCheckBox[] showVersionCheckBoxes;
  private final JLabel useVersionLabel;
  @Getter private final JComboBox<String> versionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showAlphaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showInfdevVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showVersionCheckBoxes =
        new JCheckBox[] {
          this.showBetaVersionsCheckBox,
          this.showAlphaVersionsCheckBox,
          this.showInfdevVersionsCheckBox
        };
    this.useVersionLabel = new JLabel("vgb.useVersionLabel", SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
  }

  public VersionGroupBox() {
    super("vgb.title", true);

    VersionGroupBox.instance = this;
    this.setLayout(this.getGroupLayout());

    this.showBetaVersionsCheckBox.setSelected(CONFIG.isShowBetaVersionsSelected());
    this.showAlphaVersionsCheckBox.setSelected(CONFIG.isShowAlphaVersionsSelected());
    this.showInfdevVersionsCheckBox.setSelected(CONFIG.isShowInfdevVersionsSelected());

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);

    val selectedLanguage = CONFIG.getSelectedLanguage();
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : LANGUAGE.getBundle());

    LauncherVersionUtils.updateVersionComboBox(this);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    val versionType = Collections.unmodifiableList(Arrays.asList("Beta", "Alpha", "Infdev"));
    val versionReleaseRange =
        Collections.unmodifiableList(
            Arrays.asList(
                "2010-12-20 -> 2011-09-15",
                "2010-06-30 -> 2010-12-03",
                "2010-02-27 -> 2010-06-30"));

    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showBetaVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versionType.get(0),
        versionReleaseRange.get(0));
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showAlphaVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versionType.get(1),
        versionReleaseRange.get(1));
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.showInfdevVersionsCheckBox,
        "vgb.showVersionsCheckBox",
        versionType.get(2),
        versionReleaseRange.get(2));
    LauncherUtils.updateComponentKeyValue(bundle, this.useVersionLabel, "vgb.useVersionLabel");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.LEADING)
                    .addComponent(this.showBetaVersionsCheckBox)
                    .addComponent(this.showAlphaVersionsCheckBox)
                    .addComponent(this.showInfdevVersionsCheckBox)
                    .addGroup(
                        groupLayout
                            .createSequentialGroup()
                            .addComponent(this.useVersionLabel)
                            .addComponent(this.versionComboBox))));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.showBetaVersionsCheckBox)
            .addComponent(this.showAlphaVersionsCheckBox)
            .addComponent(this.showInfdevVersionsCheckBox)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.useVersionLabel)
                    .addComponent(this.versionComboBox)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();

    for (val versionCheckBox : this.showVersionCheckBoxes) {
      if (source == versionCheckBox) {
        CONFIG.setShowBetaVersionsSelected(this.showBetaVersionsCheckBox.isSelected());
        CONFIG.setShowAlphaVersionsSelected(this.showAlphaVersionsCheckBox.isSelected());
        CONFIG.setShowInfdevVersionsSelected(this.showInfdevVersionsCheckBox.isSelected());
        LauncherVersionUtils.updateVersionComboBox(this);
        break;
      }
    }
  }
}
