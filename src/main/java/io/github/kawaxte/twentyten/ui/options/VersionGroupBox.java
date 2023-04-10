package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.val;

public class VersionGroupBox extends JGroupBox implements ActionListener {

  public static final long serialVersionUID = 1L;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JLabel useVersionLabel;
  private final JComboBox<String> versionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showAlphaVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.showInfdevVersionsCheckBox = new JCheckBox("vgb.showVersionsCheckBox");
    this.useVersionLabel = new JLabel("vgb.useVersionLabel",
        SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
  }

  public VersionGroupBox() {
    super("vgb.title", true);

    this.setLayout(this.getGroupLayout());

    this.showBetaVersionsCheckBox.addActionListener(this);
    this.showAlphaVersionsCheckBox.addActionListener(this);
    this.showInfdevVersionsCheckBox.addActionListener(this);

    this.updateComponentKeyValues();
  }

  private void updateComponentKeyValues() {
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.showBetaVersionsCheckBox,
        this.showBetaVersionsCheckBox.getText(),
        "Beta",
        "2010-12-20 -> 2011-01-21");
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.showAlphaVersionsCheckBox,
        this.showAlphaVersionsCheckBox.getText(),
        "Alpha",
        "2010-07-02 -> 2010-12-03");
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.showInfdevVersionsCheckBox,
        this.showInfdevVersionsCheckBox.getText(),
        "Infdev",
        "2010-06-29 -> 2010-06-30");
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.useVersionLabel,
        this.useVersionLabel.getText());
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createSequentialGroup()
            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(this.showBetaVersionsCheckBox)
                .addComponent(this.showAlphaVersionsCheckBox)
                .addComponent(this.showInfdevVersionsCheckBox)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(this.useVersionLabel)
                    .addComponent(this.versionComboBox))));
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.showBetaVersionsCheckBox)
            .addComponent(this.showAlphaVersionsCheckBox)
            .addComponent(this.showInfdevVersionsCheckBox)
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.useVersionLabel)
                .addComponent(this.versionComboBox)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
  }
}
