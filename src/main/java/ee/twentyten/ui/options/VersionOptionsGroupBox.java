package ee.twentyten.ui.options;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.ui.JGroupBox;
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

public class VersionOptionsGroupBox extends JGroupBox implements ActionListener {

  @Getter
  @Setter
  private static VersionOptionsGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JCheckBox[] showVersionCheckBoxes;
  private final JLabel useVersionLabel;
  @Getter
  private final JComboBox<String> useVersionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vogb.checkbox.showVersionsCheckBox", "Beta",
            "2010-12-20 -> 2011-01-21"), ConfigUtils.getInstance().isShowBetaVersionsSelected());
    this.showAlphaVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vogb.checkbox.showVersionsCheckBox", "Alpha",
            "2010-07-02 -> 2010-12-03"), ConfigUtils.getInstance().isShowAlphaVersionsSelected());
    this.showInfdevVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vogb.checkbox.showVersionsCheckBox", "Infdev",
            "2010-06-29 -> 2010-06-30"), ConfigUtils.getInstance().isShowInfdevVersionsSelected());
    this.showVersionCheckBoxes = new JCheckBox[]{this.showBetaVersionsCheckBox,
        this.showAlphaVersionsCheckBox, this.showInfdevVersionsCheckBox};
    this.useVersionLabel = new JLabel("vogb.label.useVersionLabel", JLabel.RIGHT);
    this.useVersionComboBox = new JComboBox<>();

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);
  }

  public VersionOptionsGroupBox() {
    super("vogb.string.title");

    VersionOptionsGroupBox.setInstance(this);
    this.buildTopPanel();
    this.buildMiddlePanel();

    VersionUtils.updateVersionComboBox(this);
    this.setTextToContainers(LanguageUtils.getBundle());
    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToContainers(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToContainer(bundle, this, "vogb.string.title");
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.showBetaVersionsCheckBox,
        "vogb.checkbox.showVersionsCheckBox", "Beta", "2010-12-20 -> 2011-01-21");
    LanguageUtils.setTextToComponent(bundle, this.showAlphaVersionsCheckBox,
        "vogb.checkbox.showVersionsCheckBox", "Alpha", "2010-07-02 -> 2010-12-03");
    LanguageUtils.setTextToComponent(bundle, this.showInfdevVersionsCheckBox,
        "vogb.checkbox.showVersionsCheckBox", "Infdev", "2010-06-29 -> 2010-06-30");
    LanguageUtils.setTextToComponent(bundle, this.useVersionLabel, "vogb.label.useVersionLabel");
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
      if (source.equals(this.showVersionCheckBoxes[i])) {
        switch (i) {
          case 0:
            ConfigUtils.getInstance()
                .setShowBetaVersionsSelected(this.showVersionCheckBoxes[i].isSelected());
            break;
          case 1:
            ConfigUtils.getInstance()
                .setShowAlphaVersionsSelected(this.showVersionCheckBoxes[i].isSelected());
            break;
          case 2:
            ConfigUtils.getInstance()
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
