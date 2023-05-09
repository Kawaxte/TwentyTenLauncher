package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.custom.CustomJPanel;
import io.github.kawaxte.twentyten.launcher.ui.custom.TransparentJButton;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.val;

public class LauncherOfflinePanel extends CustomJPanel implements ActionListener {

  public static LauncherOfflinePanel instance;
  private final JLabel errorLabel;
  private final JLabel playOnlineLabel;
  private final JButton playOfflineButton;
  private final JButton tryAgainButton;
  private String errorMessage;

  {
    this.errorLabel = new JLabel((String) null, SwingConstants.CENTER);
    this.playOnlineLabel = new JLabel("lop.playOnlineLabel", SwingConstants.LEFT);
    this.playOfflineButton = new TransparentJButton("lop.playOfflineButton");
    this.tryAgainButton = new TransparentJButton("lop.tryAgainButton");
  }

  public LauncherOfflinePanel() {
    super(true);

    LauncherOfflinePanel.instance = this;
    this.setLayout(this.getGroupLayout());

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle((String) selectedLanguage)
            : LauncherLanguage.bundle);
  }

  public LauncherOfflinePanel(String message) {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.errorMessage = message;
    this.errorLabel.setText(this.errorMessage);
    this.errorLabel.setFont(this.getFont().deriveFont(Font.ITALIC, 16F));
    this.errorLabel.setForeground(Color.RED.darker());

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle((String) selectedLanguage)
            : LauncherLanguage.bundle);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(bundle, this.errorLabel, this.errorMessage);
    LauncherUtils.updateComponentKeyValue(bundle, this.playOnlineLabel, "lop.playOnlineLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.playOfflineButton, "lop.playOfflineButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.tryAgainButton, "lop.tryAgainButton");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createParallelGroup()
            .addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addComponent(
                        this.playOnlineLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addComponent(this.playOfflineButton, 0, 0, Short.MAX_VALUE)
                    .addComponent(this.tryAgainButton, 0, 0, Short.MAX_VALUE)));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.playOnlineLabel))
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.playOfflineButton)
                    .addComponent(this.tryAgainButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.tryAgainButton)) {
      LauncherUtils.addComponentToContainer(this.getParent(), new YggdrasilAuthPanel());
    }
  }
}
