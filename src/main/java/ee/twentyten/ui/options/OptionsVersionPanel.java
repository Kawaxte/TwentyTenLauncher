package ee.twentyten.ui.options;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;

@Getter
public class OptionsVersionPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final String showVersionsCheckboxText;
  private final String useVersionLabelText;
  private JCheckBox showBetaVersionsCheckBox;
  private JCheckBox showAlphaVersionsCheckBox;
  private JCheckBox showInfdevVersionsCheckBox;
  private JLabel useVersionLabel;
  private JComboBox<String> useVersionComboBox;

  {
    this.showVersionsCheckboxText = LauncherLanguage.getString(
        "ovp.checkbox.showVersionsCheckBox");
    this.useVersionLabelText = LauncherLanguage.getString(
        "ovp.label.useVersionLabel");
  }

  public OptionsVersionPanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new GridLayout(0, 1), true);
    this.add(middlePanel, SwingConstants.CENTER);

    String showBetaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Beta", "2010-12-20 -> 2011-01-21");
    this.showBetaVersionsCheckBox = new JCheckBox(showBetaVersionsCheckboxText,
        LauncherConfig.instance.getUsingBeta());
    middlePanel.add(this.showBetaVersionsCheckBox, 0);

    String showAlphaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Alpha", "2010-07-02 -> 2010-12-03");
    this.showAlphaVersionsCheckBox = new JCheckBox(
        showAlphaVersionsCheckboxText, LauncherConfig.instance.getUsingAlpha());
    middlePanel.add(this.showAlphaVersionsCheckBox, 1);

    String showInfdevVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText, "Infdev", "2010-06-29 -> 2010-06-30");
    this.showInfdevVersionsCheckBox = new JCheckBox(
        showInfdevVersionsCheckboxText,
        LauncherConfig.instance.getUsingInfdev());
    middlePanel.add(this.showInfdevVersionsCheckBox, 2);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.useVersionLabel = new JLabel(this.useVersionLabelText,
        SwingConstants.RIGHT);
    bottomPanel.add(this.useVersionLabel, BorderLayout.WEST);

    this.useVersionComboBox = new JComboBox<>();
    bottomPanel.add(this.useVersionComboBox, BorderLayout.CENTER);
  }
}
