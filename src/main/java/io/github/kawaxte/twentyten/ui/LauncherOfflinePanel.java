package io.github.kawaxte.twentyten.ui;

import io.github.kawaxte.twentyten.misc.ui.CustomJLabel;
import io.github.kawaxte.twentyten.misc.ui.CustomJPanel;
import io.github.kawaxte.twentyten.misc.ui.TransparentJButton;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class LauncherOfflinePanel extends CustomJPanel implements ActionListener {

  @Getter
  private final CustomJLabel errorLabel;
  private final JLabel playOnlineLabel;
  private final JButton playOfflineButton;
  private final JButton tryAgainButton;

  {
    this.errorLabel = new CustomJLabel("",
        SwingConstants.CENTER,
        CustomJLabel.ERROR);
    this.playOnlineLabel = new JLabel("llp.playOnlineLabel",
        SwingConstants.LEFT);
    this.playOfflineButton = new TransparentJButton("llp.playOfflineButton");
    this.tryAgainButton = new TransparentJButton("llp.tryAgainButton");
  }

  public LauncherOfflinePanel() {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);

    this.updateComponentKeyValues();
  }

  public LauncherOfflinePanel(String message) {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.errorLabel.setText(message);

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);

    this.updateComponentKeyValues();
  }

  private void updateComponentKeyValues() {
    if (!this.errorLabel.getText().isEmpty()) {
      LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
          this.errorLabel,
          this.errorLabel.getText());
    }
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.playOnlineLabel,
        this.playOnlineLabel.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.playOfflineButton,
        this.playOfflineButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.tryAgainButton,
        this.tryAgainButton.getText());
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
        .addComponent(this.errorLabel,
            0,
            GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE)
        .addGroup(groupLayout.createSequentialGroup()
            .addComponent(this.playOnlineLabel)
            .addGap(0, 0, Short.MAX_VALUE))
        .addGroup(groupLayout.createSequentialGroup()
            .addComponent(this.playOfflineButton,
                0,
                0,
                Short.MAX_VALUE)
            .addComponent(this.tryAgainButton,
                0,
                0,
                Short.MAX_VALUE)));
    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
        .addComponent(this.errorLabel,
            0,
            GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE)
        .addGroup(groupLayout.createParallelGroup()
            .addComponent(this.playOnlineLabel))
        .addGroup(groupLayout.createParallelGroup()
            .addComponent(this.playOfflineButton)
            .addComponent(this.tryAgainButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.tryAgainButton)) {
      LauncherUtils.addPanel(this.getParent(),
          new YggdrasilLoginPanel());
    }
  }
}
