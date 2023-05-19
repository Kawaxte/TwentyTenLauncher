/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.Launcher;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.game.GameUpdater;
import io.github.kawaxte.twentyten.launcher.ui.custom.CustomJPanel;
import io.github.kawaxte.twentyten.launcher.ui.custom.TransparentJButton;
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

public class LauncherNoNetworkPanel extends CustomJPanel implements ActionListener {

  public static LauncherNoNetworkPanel instance;
  private final JLabel errorLabel;
  private final JLabel playOnlineLabel;
  private final JButton playOfflineButton;
  private final JButton retryButton;
  private final String errorMessage;
  private final Object[] errorMessageArgs;

  {
    this.errorLabel = new JLabel((String) null, SwingConstants.CENTER);
    this.playOnlineLabel = new JLabel("lnnp.playOnlineLabel", SwingConstants.LEFT);
    this.playOfflineButton = new TransparentJButton("lnnp.playOfflineButton");
    this.retryButton = new TransparentJButton("lnnp.retryButton");
  }

  public LauncherNoNetworkPanel(String key, Object... args) {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.errorMessage = key;
    this.errorMessageArgs = args;
    this.errorLabel.setText(this.errorMessage);
    this.errorLabel.setFont(this.getFont().deriveFont(Font.ITALIC, 16F));
    this.errorLabel.setForeground(Color.RED.darker());
    this.playOfflineButton.setEnabled(GameUpdater.isGameCached());
    this.playOnlineLabel.setVisible(!GameUpdater.isGameCached());

    this.playOfflineButton.addActionListener(this);
    this.retryButton.addActionListener(this);

    val selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    val bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.bundle);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle, this.errorLabel, this.errorMessage, this.errorMessageArgs);
    LauncherUtils.updateComponentKeyValue(bundle, this.playOnlineLabel, "lnnp.playOnlineLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.playOfflineButton, "lnnp.playOfflineButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.retryButton, "lnnp.retryButton");
  }

  private LayoutManager getGroupLayout() {
    int width = 0;

    val buttons = new JButton[] {this.playOfflineButton, this.retryButton};
    for (val button : buttons) {
      width = Math.max(width, button.getPreferredSize().width);
    }

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
                    .addComponent(this.playOfflineButton, 0, width, Short.MAX_VALUE)
                    .addComponent(this.retryButton, 0, width, Short.MAX_VALUE)));
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
                    .createParallelGroup(Alignment.CENTER)
                    .addComponent(this.playOfflineButton)
                    .addComponent(this.retryButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.playOfflineButton)) {
      Launcher.launchMinecraft(null, null, null);
    }
    if (Objects.equals(source, this.retryButton)) {
      LauncherUtils.swapContainers(this.getParent(), new YggdrasilAuthPanel());
    }
  }
}
