package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import java.text.MessageFormat;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.val;

public class VersionGroupBox extends JGroupBox {

  public static final long serialVersionUID = 1L;
  private final JCheckBox showBetaVersionsCheckBox;
  private final JCheckBox showAlphaVersionsCheckBox;
  private final JCheckBox showInfdevVersionsCheckBox;
  private final JLabel useVersionLabel;
  private final JComboBox<String> versionComboBox;

  {
    this.showBetaVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vgb.showVersionsCheckBox",
            "Beta",
            "2010-12-20 -> 2011-01-21"));
    this.showAlphaVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vgb.showVersionsCheckBox",
            "Alpha",
            "2010-07-02 -> 2010-12-03"));
    this.showInfdevVersionsCheckBox = new JCheckBox(
        MessageFormat.format("vgb.showVersionsCheckBox",
            "Infdev",
            "2010-06-29 -> 2010-06-30"));
    this.useVersionLabel = new JLabel("vgb.useVersionLabel", SwingConstants.RIGHT);
    this.versionComboBox = new JComboBox<>();
  }

  public VersionGroupBox() {
    super("vgb.title", true);

    this.setLayout(this.getGroupLayout());
  }

  private GroupLayout getGroupLayout() {
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
                    .addComponent(this.versionComboBox)
                )
            )
    );
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.showBetaVersionsCheckBox)
            .addComponent(this.showAlphaVersionsCheckBox)
            .addComponent(this.showInfdevVersionsCheckBox)
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.useVersionLabel)
                .addComponent(this.versionComboBox)
            )
    );
    return groupLayout;
  }
}
