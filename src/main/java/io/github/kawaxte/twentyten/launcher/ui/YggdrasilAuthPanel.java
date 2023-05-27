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
import io.github.kawaxte.twentyten.launcher.ui.custom.CustomJPanel;
import io.github.kawaxte.twentyten.launcher.ui.custom.JHyperlink;
import io.github.kawaxte.twentyten.launcher.ui.custom.TransparentJButton;
import io.github.kawaxte.twentyten.launcher.ui.custom.TransparentJCheckBox;
import io.github.kawaxte.twentyten.launcher.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import io.github.kawaxte.twentyten.launcher.util.MicrosoftAuthUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Class representing the {@link javax.swing.JPanel} that contains various interactive components.
 *
 * <p>It also handles any action that the user may perform on the components.
 *
 * @author Kawaxte
 * @since 1.5.0623_01
 */
public class YggdrasilAuthPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static YggdrasilAuthPanel instance;
  private final TransparentJButton microsoftSigninButton;
  private final JLabel usernameLabel;
  private final JLabel passwordLabel;
  private final JTextField usernameField;
  private final JPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final JHyperlink linkLabel;
  private final TransparentJButton signinButton;

  /**
   * Constructor for YggdrasilAuthPanel.
   *
   * <p>Initialises the components and sets the layout. Also, adds the action listeners to the some
   * of the components that require it and sets the component texts according to the currently
   * selected language.
   *
   * <p>It also handles the click event for the {@link #linkLabel} component, which opens the
   * browser and navigates to the link specified in {@link
   * io.github.kawaxte.twentyten.launcher.util.LauncherUtils#getLinkLabelUrls()}.
   *
   * @see #setLayout(LayoutManager)
   * @see #updateComponentTexts(UTF8ResourceBundle)
   */
  public YggdrasilAuthPanel() {
    super(true);

    setInstance(this);

    this.microsoftSigninButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[0]);
    this.usernameLabel = new JLabel(LauncherLanguageUtils.getYAPKeys()[2], SwingConstants.RIGHT);
    this.passwordLabel = new JLabel(LauncherLanguageUtils.getYAPKeys()[3], SwingConstants.RIGHT);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.optionsButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[4]);
    this.rememberPasswordCheckBox = new TransparentJCheckBox(LauncherLanguageUtils.getYAPKeys()[5]);
    this.linkLabel =
        new JHyperlink(
            Objects.nonNull(LauncherUtils.getOutdated())
                    && Boolean.TRUE.equals(LauncherUtils.getOutdated())
                ? LauncherLanguageUtils.getYAPKeys()[7]
                : LauncherLanguageUtils.getYAPKeys()[6],
            SwingConstants.LEFT);
    this.signinButton = new TransparentJButton(LauncherLanguageUtils.getYAPKeys()[8]);

    this.setLayout(this.getGroupLayout());

    Object mojangUsername = LauncherConfig.get(11);
    String mojangPassword = LauncherUtils.decodeFromBase64(12);
    Object mojangRememberPasswordChecked = LauncherConfig.get(13);
    if (Objects.nonNull(mojangUsername)) {
      this.usernameField.setText((String) mojangUsername);
    }
    this.passwordField.setText(mojangPassword);
    this.rememberPasswordCheckBox.setSelected(
        Boolean.parseBoolean(mojangRememberPasswordChecked.toString()));

    this.microsoftSigninButton.addActionListener(this);
    this.optionsButton.addActionListener(this);
    this.linkLabel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent event) {
            LauncherUtils.openBrowser(
                Objects.nonNull(LauncherUtils.getOutdated())
                        && Boolean.TRUE.equals(LauncherUtils.getOutdated())
                    ? String.valueOf(LauncherUtils.getLinkLabelUrls()[1])
                    : String.valueOf(LauncherUtils.getLinkLabelUrls()[0]));
          }
        });
    this.signinButton.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentTexts(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
  }

  public static YggdrasilAuthPanel getInstance() {
    return instance;
  }

  private static void setInstance(YggdrasilAuthPanel yap) {
    YggdrasilAuthPanel.instance = yap;
  }

  /**
   * Updates the texts of the components.
   *
   * <p>The texts are set according to the provided {@link
   * io.github.kawaxte.twentyten.UTF8ResourceBundle}.
   *
   * @param bundle the {@link io.github.kawaxte.twentyten.UTF8ResourceBundle} containing the
   *     localised keys and values in the resource bundle
   */
  public void updateComponentTexts(UTF8ResourceBundle bundle) {
    LauncherUtils.setComponentText(
        bundle, this.microsoftSigninButton, LauncherLanguageUtils.getYAPKeys()[0]);
    LauncherUtils.setComponentText(
        bundle, this.usernameLabel, LauncherLanguageUtils.getYAPKeys()[2]);
    LauncherUtils.setComponentText(
        bundle, this.passwordLabel, LauncherLanguageUtils.getYAPKeys()[3]);
    LauncherUtils.setComponentText(
        bundle, this.optionsButton, LauncherLanguageUtils.getYAPKeys()[4]);
    LauncherUtils.setComponentText(
        bundle, this.rememberPasswordCheckBox, LauncherLanguageUtils.getYAPKeys()[5]);
    LauncherUtils.setComponentText(
        bundle,
        this.linkLabel,
        Objects.nonNull(LauncherUtils.getOutdated())
                && Boolean.TRUE.equals(LauncherUtils.getOutdated())
            ? LauncherLanguageUtils.getYAPKeys()[7]
            : LauncherLanguageUtils.getYAPKeys()[6]);
    LauncherUtils.setComponentText(
        bundle, this.signinButton, LauncherLanguageUtils.getYAPKeys()[8]);
  }

  /**
   * Creates and returns the {@link javax.swing.GroupLayout} used to layout the components in the
   * panel.
   *
   * @return the layout of the panel
   */
  private LayoutManager getGroupLayout() {
    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createParallelGroup(Alignment.CENTER)
            .addComponent(this.microsoftSigninButton, 0, 0, Short.MAX_VALUE)
            .addGroup(
                gl.createSequentialGroup()
                    .addGroup(
                        gl.createParallelGroup(Alignment.LEADING)
                            .addComponent(this.usernameLabel, Alignment.TRAILING)
                            .addComponent(this.passwordLabel, Alignment.TRAILING)
                            .addComponent(this.optionsButton, Alignment.TRAILING))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        gl.createParallelGroup(Alignment.LEADING)
                            .addComponent(this.usernameField)
                            .addComponent(this.passwordField)
                            .addComponent(this.rememberPasswordCheckBox)))
            .addGroup(
                gl.createSequentialGroup()
                    .addComponent(this.linkLabel)
                    .addPreferredGap(
                        ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.signinButton)));
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.microsoftSigninButton)
            .addPreferredGap(
                ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.usernameLabel)
                    .addComponent(this.usernameField))
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.passwordLabel)
                    .addComponent(this.passwordField))
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.optionsButton)
                    .addComponent(this.rememberPasswordCheckBox))
            .addGroup(
                gl.createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.linkLabel)
                    .addComponent(this.signinButton)));
    return gl;
  }

  /**
   * Handles the actions performed on {@link #microsoftSigninButton}, {@link #optionsButton}, and
   * {@link #signinButton}.
   *
   * <p>Currently, the following components are handled:
   *
   * <ul>
   *   <li>{@link #microsoftSigninButton}
   *   <li>{@link #optionsButton}
   *   <li>{@link #signinButton}
   * </ul>
   *
   * <p>When processing either {@link #microsoftSigninButton} or {@link #signinButton}, the
   * following actions are performed:
   *
   * <ol>
   *   <li>Disable all components in the panel
   *   <li>Set the text of the button to "Signing in..."
   *   <li>Check if the launcher is outdated
   *   <li>If the launcher is outdated, swap the panel with a {@link
   *       io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel}
   *   <li>Otherwise, check if the username and password fields are empty
   *   <li>If the username and password fields are empty, swap the panel with a {@link
   *       io.github.kawaxte.twentyten.launcher.ui.LauncherNoNetworkPanel} and display an error
   *       message, otherwise continue
   * </ol>
   *
   * <p>If everything goes flawlessly, Minecraft will be launched right after checking for updates.
   *
   * @param event the action event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent event) {
    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);

    Object source = event.getSource();
    if (Objects.equals(source, this.microsoftSigninButton)) {
      Arrays.stream(this.getComponents()).forEachOrdered(component -> component.setEnabled(false));
      this.microsoftSigninButton.setText(bundle.getString(LauncherLanguageUtils.getYAPKeys()[1]));

      if (LauncherUtils.isOutdated()) {
        LauncherUtils.swapContainers(
            this.getParent(), new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[2]));
        return;
      }

      String microsoftProfileName = (String) LauncherConfig.get(6);
      String microsoftProfileId = (String) LauncherConfig.get(5);
      String microsoftAccessToken = (String) LauncherConfig.get(7);
      boolean accessTokenMatched =
          LauncherUtils.JWT_PATTERN.matcher(microsoftAccessToken).matches();
      if (!accessTokenMatched) {
        MicrosoftAuthUtils.executeMicrosoftAuthWorker(MicrosoftAuthUtils.AZURE_CLIENT_ID);
        return;
      }

      Launcher.launchMinecraft(microsoftProfileName, microsoftAccessToken, microsoftProfileId);
    }
    if (Objects.equals(source, this.optionsButton)) {
      SwingUtilities.invokeLater(
          () -> new OptionsDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));
    }
    if (Objects.equals(source, this.signinButton)) {
      Arrays.stream(this.getComponents()).forEachOrdered(component -> component.setEnabled(false));
      this.signinButton.setText(bundle.getString(LauncherLanguageUtils.getYAPKeys()[9]));

      if (LauncherUtils.isOutdated()) {
        LauncherUtils.swapContainers(
            this.getParent(), new LauncherNoNetworkPanel(LauncherLanguageUtils.getLNPPKeys()[2]));
        return;
      }

      String username = this.usernameField.getText();
      String password = new String(this.passwordField.getPassword());
      boolean rememberPasswordChecked = this.rememberPasswordCheckBox.isSelected();

      Object mojangUsername = LauncherConfig.get(11);
      Object mojangPassword = LauncherUtils.decodeFromBase64(12);
      String mojangProfileName = (String) LauncherConfig.get(15);
      String mojangProfileId = (String) LauncherConfig.get(14);
      String mojangAccessToken = (String) LauncherConfig.get(17);
      String mojangClientToken = (String) LauncherConfig.get(18);

      boolean usernameChanged = Objects.equals(mojangUsername, username);
      boolean passwordChanged = Objects.equals(mojangPassword, password) && !password.isEmpty();
      boolean accessTokenMatched =
          Objects.nonNull(mojangAccessToken)
              && LauncherUtils.JWT_PATTERN.matcher(mojangAccessToken).matches();
      boolean clientTokenMatched =
          Objects.nonNull(mojangClientToken)
              && LauncherUtils.UUID_PATTERN.matcher(mojangClientToken).matches();
      if ((!usernameChanged || !passwordChanged) || (!accessTokenMatched || !clientTokenMatched)) {
        YggdrasilAuthUtils.executeYggdrasilAuthWorker(
            username, password, mojangClientToken, rememberPasswordChecked);
        return;
      }

      Launcher.launchMinecraft(mojangProfileName, mojangAccessToken, mojangProfileId);
    }
  }
}
