package io.github.kawaxte.twentyten.ui.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
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

    this.okButton = new JButton("od.okButton");
    this.okButton.addActionListener(this);

    this.cancelButton = new JButton("od.cancelButton");
    this.cancelButton.addActionListener(this);

    this.applyButton = new JButton("od.applyButton");
    this.applyButton.addActionListener(this);
  }

  public OptionsPanel() {
    super(true);

    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);

    groupLayout.setHorizontalGroup(
        groupLayout.createSequentialGroup()
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(this.languageGroupBox)
                .addComponent(this.versionGroupBox)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(this.okButton)
                    .addComponent(this.cancelButton)
                    .addComponent(this.applyButton)
                )
            )
    );
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.languageGroupBox)
            .addComponent(this.versionGroupBox)
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(this.okButton)
                .addComponent(this.cancelButton)
                .addComponent(this.applyButton)
            )
    );

    this.setLayout(groupLayout);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
  }
}
