package ee.twentyten.ui.options;

import ee.twentyten.config.LauncherConfig;
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
public class OptionsVersionsPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  JCheckBox showBetaVersionsCheckBox;
  JCheckBox showAlphaVersionsCheckBox;
  JCheckBox showInfdevVersionsCheckBox;
  JLabel useVersionLabel;
  JComboBox<String> versionComboBox;

  public OptionsVersionsPanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.initComponents();
  }

  private void initComponents() {
    this.showBetaVersionsCheckBox = new JCheckBox(
        "Show \"Beta\" versions of Minecraft (2010-12-20 -> 2011-01-21)",
        LauncherConfig.instance.getUsingBeta());
    this.showAlphaVersionsCheckBox = new JCheckBox(
        "Show \"Alpha\" versions of Minecraft (2010-07-02 -> 2010-12-03)",
        LauncherConfig.instance.getUsingAlpha());
    this.showInfdevVersionsCheckBox = new JCheckBox(
        "Show \"Infdev\" versions of Minecraft (2010-06-29 -> 2010-06-30)",
        LauncherConfig.instance.getUsingInfdev());
    this.createMiddlePanel();

    this.useVersionLabel = new JLabel("Use version:", SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new GridLayout(0, 1), true);
    middlePanel.add(this.showBetaVersionsCheckBox, 0);
    middlePanel.add(this.showAlphaVersionsCheckBox, 1);
    middlePanel.add(this.showInfdevVersionsCheckBox, 2);
    this.add(middlePanel, SwingConstants.CENTER);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.add(this.useVersionLabel, BorderLayout.WEST);
    bottomPanel.add(this.versionComboBox, BorderLayout.CENTER);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
