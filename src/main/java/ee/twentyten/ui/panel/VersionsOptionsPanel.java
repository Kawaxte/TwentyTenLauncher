package ee.twentyten.ui.panel;

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
public class VersionsOptionsPanel extends JPanel {

  JCheckBox showBetaVersionsCheckBox;
  JCheckBox showAlphaVersionsCheckBox;
  JCheckBox showInfdevVersionsCheckBox;
  JLabel useVersionLabel;
  JComboBox<String> versionComboBox;

  public VersionsOptionsPanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.initComponents();
  }

  private void initComponents() {
    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new GridLayout(0, 1), true);
    this.showBetaVersionsCheckBox = new JCheckBox(
        "Show \"Beta\" versions of Minecraft (2010-12-20 -> 2011-01-21)");
    this.showAlphaVersionsCheckBox = new JCheckBox(
        "Show \"Alpha\" versions of Minecraft (2010-07-02 -> 2010-12-03)");
    this.showInfdevVersionsCheckBox = new JCheckBox(
        "Show \"Infdev\" versions of Minecraft (2010-06-29 -> 2010-06-30)");
    middlePanel.add(this.showBetaVersionsCheckBox, 0);
    middlePanel.add(this.showAlphaVersionsCheckBox, 1);
    middlePanel.add(this.showInfdevVersionsCheckBox, 2);
    this.add(middlePanel, SwingConstants.CENTER);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.useVersionLabel = new JLabel("Use version:", SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
    bottomPanel.add(this.useVersionLabel, BorderLayout.WEST);
    bottomPanel.add(this.versionComboBox, BorderLayout.CENTER);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
