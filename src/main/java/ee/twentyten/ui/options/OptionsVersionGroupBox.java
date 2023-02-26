package ee.twentyten.ui.options;

import ee.twentyten.custom.JGroupBox;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.util.LanguageUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class OptionsVersionGroupBox extends JGroupBox implements ActionListener {

  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  private static OptionsVersionGroupBox instance;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JLabel useVersionLabel;
  private final JComboBox<String> useVersionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox(
        String.format(LanguageUtils.showVersionsCheckBoxKey, "Beta", "2010-12-20 -> 2011-01-21"));
    this.showAlphaVersionsCheckBox = new JCheckBox(
        String.format(LanguageUtils.showVersionsCheckBoxKey, "Alpha", "2010-07-02 -> 2010-12-03"));
    this.showInfdevVersionsCheckBox = new JCheckBox(
        String.format(LanguageUtils.showVersionsCheckBoxKey, "Infdev", "2010-06-29 -> 2010-06-30"));
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
  }
}
