package ee.twentyten.ui.options;

import ee.twentyten.custom.JGroupBox;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.VersionUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class OptionsVersionGroupBox extends JGroupBox implements ActionListener {

  @Getter
  @Setter
  public static OptionsVersionGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JCheckBox[] showVersionCheckBoxes;
  private final JLabel useVersionLabel;
  @Getter
  private final JComboBox<String> useVersionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox(
        MessageFormat.format(LanguageUtils.showVersionsCheckBoxKey, "Beta",
            "2010-12-20 -> 2011-01-21"), ConfigUtils.getConfig().isShowBetaVersionsSelected());
    this.showAlphaVersionsCheckBox = new JCheckBox(
        MessageFormat.format(LanguageUtils.showVersionsCheckBoxKey, "Alpha",
            "2010-07-02 -> 2010-12-03"), ConfigUtils.getConfig().isShowAlphaVersionsSelected());
    this.showInfdevVersionsCheckBox = new JCheckBox(
        MessageFormat.format(LanguageUtils.showVersionsCheckBoxKey, "Infdev",
            "2010-06-29 -> 2010-06-30"), ConfigUtils.getConfig().isShowInfdevVersionsSelected());
    this.showVersionCheckBoxes = new JCheckBox[]{this.showBetaVersionsCheckBox,
        this.showAlphaVersionsCheckBox, this.showInfdevVersionsCheckBox};
    this.useVersionLabel = new JLabel(LanguageUtils.useVersionLabelKey, JLabel.RIGHT);
    this.useVersionComboBox = new JComboBox<>();

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);
  }

  public OptionsVersionGroupBox() {
    super(LanguageUtils.optionsVersionGroupBoxKey);

    OptionsVersionGroupBox.setInstance(this);
    this.buildTopPanel();
    this.buildMiddlePanel();

    VersionUtils.updateVersionComboBox(this);
    this.setTextToContainers(LanguageUtils.getBundle());
    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToContainers(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToContainer(bundle, this, LanguageUtils.optionsVersionGroupBoxKey);
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.showBetaVersionsCheckBox,
        LanguageUtils.showVersionsCheckBoxKey, "Beta", "2010-12-20 -> 2011-01-21");
    LanguageUtils.setTextToComponent(bundle, this.showAlphaVersionsCheckBox,
        LanguageUtils.showVersionsCheckBoxKey, "Alpha", "2010-07-02 -> 2010-12-03");
    LanguageUtils.setTextToComponent(bundle, this.showInfdevVersionsCheckBox,
        LanguageUtils.showVersionsCheckBoxKey, "Infdev", "2010-06-29 -> 2010-06-30");
    LanguageUtils.setTextToComponent(bundle, this.useVersionLabel,
        LanguageUtils.useVersionLabelKey);
  }

  private void buildTopPanel() {
    JPanel topPanel = new JPanel(new GridLayout(3, 1), true);
    topPanel.add(this.showBetaVersionsCheckBox, 0);
    topPanel.add(this.showAlphaVersionsCheckBox, 1);
    topPanel.add(this.showInfdevVersionsCheckBox, 2);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.add(this.useVersionLabel, BorderLayout.WEST);
    middlePanel.add(this.useVersionComboBox, BorderLayout.CENTER);
    this.add(middlePanel, BorderLayout.CENTER);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();

    for (int i = 0; i < this.showVersionCheckBoxes.length; i++) {
      if (source == this.showVersionCheckBoxes[i]) {
        switch (i) {
          case 0:
            ConfigUtils.getConfig()
                .setShowBetaVersionsSelected(this.showVersionCheckBoxes[i].isSelected());
            break;
          case 1:
            ConfigUtils.getConfig()
                .setShowAlphaVersionsSelected(this.showVersionCheckBoxes[i].isSelected());
            break;
          case 2:
            ConfigUtils.getConfig()
                .setShowInfdevVersionsSelected(this.showVersionCheckBoxes[i].isSelected());
            break;
          default:
            break;
        }
        VersionUtils.updateVersionComboBox(this);
      }
    }
  }
}
