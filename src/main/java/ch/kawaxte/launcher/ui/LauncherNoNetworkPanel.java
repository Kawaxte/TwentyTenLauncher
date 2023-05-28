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

package ch.kawaxte.launcher.ui;

import ch.kawaxte.launcher.Launcher;
import ch.kawaxte.launcher.LauncherConfig;
import ch.kawaxte.launcher.LauncherLanguage;
import ch.kawaxte.launcher.impl.UTF8ResourceBundle;
import ch.kawaxte.launcher.impl.swing.CustomJPanel;
import ch.kawaxte.launcher.impl.swing.TransparentJButton;
import ch.kawaxte.launcher.minecraft.MinecraftUpdate;
import ch.kawaxte.launcher.util.LauncherLanguageUtils;
import ch.kawaxte.launcher.util.LauncherUtils;
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

/**
 * Class representing the {@link javax.swing.JPanel} that contains various interactive components.
 *
 * <p>It also handles any action that the user may perform on the components.
 *
 * @author Kawaxte
 * @since 1.4.1023_03
 */
public class LauncherNoNetworkPanel extends CustomJPanel implements ActionListener {

  private static LauncherNoNetworkPanel instance;
  private final JLabel errorLabel;
  private final JLabel playOnlineLabel;
  private final JButton playOfflineButton;
  private final JButton retryButton;
  private final String errorMessage;
  private final transient Object[] errorMessageArgs;

  /**
   * Constructor for LauncherNoNetworkPanel.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it and sets the component texts according to the currently
   * selected language.
   *
   * @see #setLayout(LayoutManager)
   * @see #updateComponentTexts(UTF8ResourceBundle)
   */
  public LauncherNoNetworkPanel(String key, Object... args) {
    super(true);

    setInstance(this);

    this.errorMessage = key;
    this.errorMessageArgs = args;
    this.errorLabel = new JLabel((String) null, SwingConstants.CENTER);
    this.playOnlineLabel = new JLabel(LauncherLanguageUtils.getLNPPKeys()[5], SwingConstants.LEFT);
    this.playOfflineButton = new TransparentJButton(LauncherLanguageUtils.getLNPPKeys()[6]);
    this.retryButton = new TransparentJButton(LauncherLanguageUtils.getLNPPKeys()[7]);

    this.setLayout(this.getGroupLayout());

    this.errorLabel.setText(this.errorMessage);
    this.errorLabel.setFont(this.getFont().deriveFont(Font.ITALIC, 16F));
    this.errorLabel.setForeground(Color.RED.darker());
    this.playOfflineButton.setEnabled(MinecraftUpdate.isGameCached());
    this.playOnlineLabel.setVisible(!MinecraftUpdate.isGameCached());

    this.playOfflineButton.addActionListener(this);
    this.retryButton.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentTexts(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
  }

  public static LauncherNoNetworkPanel getInstance() {
    return instance;
  }

  private static void setInstance(LauncherNoNetworkPanel lnnp) {
    instance = lnnp;
  }

  /**
   * Updates the texts of the components.
   *
   * <p>The texts are set according to the provided {@link UTF8ResourceBundle}.
   *
   * @param bundle the {@link UTF8ResourceBundle} containing the localised keys and values in the
   *     resource bundle
   */
  public void updateComponentTexts(UTF8ResourceBundle bundle) {
    LauncherUtils.setComponentText(
        bundle, this.errorLabel, this.errorMessage, this.errorMessageArgs);
    LauncherUtils.setComponentText(
        bundle, this.playOnlineLabel, LauncherLanguageUtils.getLNPPKeys()[5]);
    LauncherUtils.setComponentText(
        bundle, this.playOfflineButton, LauncherLanguageUtils.getLNPPKeys()[6]);
    LauncherUtils.setComponentText(
        bundle, this.retryButton, LauncherLanguageUtils.getLNPPKeys()[7]);
  }

  /**
   * Creates and returns the {@link javax.swing.GroupLayout} used to layout the components in the
   * panel.
   *
   * @return the layout of the panel
   */
  private LayoutManager getGroupLayout() {
    int width = 0;

    JButton[] buttons = new JButton[] {this.playOfflineButton, this.retryButton};
    for (JButton button : buttons) {
      width = Math.max(width, button.getPreferredSize().width);
    }

    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createParallelGroup()
            .addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(
                gl.createSequentialGroup()
                    .addComponent(
                        this.playOnlineLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
            .addGroup(
                gl.createSequentialGroup()
                    .addComponent(this.playOfflineButton, 0, width, Short.MAX_VALUE)
                    .addComponent(this.retryButton, 0, width, Short.MAX_VALUE)));
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.errorLabel, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(this.playOnlineLabel))
            .addGroup(
                gl.createParallelGroup(Alignment.CENTER)
                    .addComponent(this.playOfflineButton)
                    .addComponent(this.retryButton)));
    return gl;
  }

  /**
   * Handles the actions performed on {@link #playOfflineButton} and {@link #retryButton}.
   *
   * <p>When processing {@link #playOfflineButton}, an 'offline' Minecraft session is launched. This
   * will only be possible if the minecraft is cached. If the minecraft is not cached, the button
   * will be disabled, and {@link #playOnlineLabel} will be shown to the user to indicate that they
   * need to play with an internet connection to download the minecraft first before they can play
   * offline later.
   *
   * <p>When processing {@link #retryButton}, the {@link LauncherPanel} is swapped with a new {@link
   * YggdrasilAuthPanel}, so that the user can try to authenticate again.
   *
   * @param event the action event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (Objects.equals(source, this.playOfflineButton)) {
      Launcher.launchMinecraft(null, null, null);
    }
    if (Objects.equals(source, this.retryButton)) {
      LauncherUtils.swapContainers(this.getParent(), new YggdrasilAuthPanel());
    }
  }
}
