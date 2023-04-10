package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import lombok.val;

public class OptionsPanel extends JPanel implements ActionListener {

  public static final long serialVersionUID = 1L;
  private final LanguageGroupBox languageGroupBox;
  private final VersionGroupBox versionGroupBox;
  private final JButton okButton;
  private final JButton cancelButton;
  private final JButton applyButton;

  {
    this.languageGroupBox = new LanguageGroupBox();
    this.versionGroupBox = new VersionGroupBox();
    this.okButton = new JButton("op.okButton");
    this.cancelButton = new JButton("op.cancelButton");
    this.applyButton = new JButton("op.applyButton");
  }

  public OptionsPanel() {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.cancelButton.addActionListener(this);
    this.okButton.addActionListener(this);
    this.applyButton.addActionListener(this);

    this.updateComponentKeyValues();
  }

  private void updateComponentKeyValues() {
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.languageGroupBox,
        this.languageGroupBox.setTitledBorder("op.languageGroupBox"));
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.versionGroupBox,
        this.versionGroupBox.setTitledBorder("op.versionGroupBox"));
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.okButton,
        this.okButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.cancelButton,
        this.cancelButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.applyButton,
        this.applyButton.getText());
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createSequentialGroup()
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(this.languageGroupBox)
                .addComponent(this.versionGroupBox)
                .addGroup(groupLayout.createSequentialGroup()
                    .addPreferredGap(ComponentPlacement.RELATED,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.okButton,
                        0,
                        0,
                        Short.MAX_VALUE)
                    .addComponent(this.cancelButton,
                        0,
                        0,
                        Short.MAX_VALUE)
                    .addComponent(this.applyButton,
                        0,
                        0,
                        Short.MAX_VALUE))));
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.languageGroupBox)
            .addComponent(this.versionGroupBox)
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.okButton)
                .addComponent(this.cancelButton)
                .addComponent(this.applyButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.cancelButton)) {
      SwingUtilities.getWindowAncestor(this).dispose();
    }
  }
}
