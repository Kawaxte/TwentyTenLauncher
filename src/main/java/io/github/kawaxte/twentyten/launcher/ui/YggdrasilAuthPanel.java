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

public class YggdrasilAuthPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  public static YggdrasilAuthPanel instance;
  private final TransparentJButton microsoftSigninButton;
  private final JLabel usernameLabel;
  private final JLabel passwordLabel;
  private final JTextField usernameField;
  private final JPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final JHyperlink linkLabel;
  private final TransparentJButton signinButton;

  {
    this.microsoftSigninButton = new TransparentJButton("yap.microsoftSigninButton");
    this.usernameLabel = new JLabel("yap.usernameLabel", SwingConstants.RIGHT);
    this.passwordLabel = new JLabel("yap.passwordLabel", SwingConstants.RIGHT);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.optionsButton = new TransparentJButton("yap.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("yap.rememberPasswordCheckBox");
    this.linkLabel =
        new JHyperlink(
            Objects.nonNull(LauncherUtils.outdated) && LauncherUtils.outdated
                ? "yap.linkLabel.outdated"
                : "yap.linkLabel",
            SwingConstants.LEFT);
    this.signinButton = new TransparentJButton("yap.signinButton");
  }

  public YggdrasilAuthPanel() {
    super(true);

    YggdrasilAuthPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    Object mojangUsername = LauncherConfig.lookup.get("mojangUsername");
    Object mojangPassword = LauncherConfig.lookup.get("mojangPassword");
    Object mojangRememberPasswordChecked =
        LauncherConfig.lookup.get("mojangRememberPasswordChecked");
    if (Objects.nonNull(mojangUsername)) {
      this.usernameField.setText((String) mojangUsername);
    }
    if (Objects.nonNull(mojangPassword)) {
      this.passwordField.setText((String) mojangPassword);
    }
    this.rememberPasswordCheckBox.setSelected(
        Boolean.parseBoolean(mojangRememberPasswordChecked.toString()));

    this.microsoftSigninButton.addActionListener(this);
    this.optionsButton.addActionListener(this);
    this.linkLabel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent event) {
            LauncherUtils.openBrowser(
                Objects.nonNull(LauncherUtils.outdated) && LauncherUtils.outdated
                    ? String.valueOf(LauncherUtils.releasesUrl)
                    : String.valueOf(LauncherUtils.signupUrl));
          }
        });
    this.signinButton.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.bundle);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle, this.microsoftSigninButton, "yap.microsoftSigninButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.usernameLabel, "yap.usernameLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.passwordLabel, "yap.passwordLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.optionsButton, "yap.optionsButton");
    LauncherUtils.updateComponentKeyValue(
        bundle, this.rememberPasswordCheckBox, "yap.rememberPasswordCheckBox");
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.linkLabel,
        Objects.nonNull(LauncherUtils.outdated) && LauncherUtils.outdated
            ? "yap.linkLabel.outdated"
            : "yap.linkLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.signinButton, "yap.signinButton");
  }

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

  @Override
  public void actionPerformed(ActionEvent event) {
    String selectedLanguage = (String) LauncherConfig.lookup.get("selectedLanguage");
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);

    Object source = event.getSource();
    if (Objects.equals(source, this.microsoftSigninButton)) {
      Arrays.stream(this.getComponents()).forEachOrdered(component -> component.setEnabled(false));
      this.microsoftSigninButton.setText(bundle.getString("yap.microsoftSigninButton.signing_in"));

      if (LauncherUtils.isOutdated()) {
        LauncherUtils.swapContainers(
            this.getParent(), new LauncherNoNetworkPanel("lnnp.errorLabel.signin_outdated"));
        return;
      }

      String microsoftProfileName = (String) LauncherConfig.lookup.get("microsoftProfileName");
      String microsoftProfileId = (String) LauncherConfig.lookup.get("microsoftProfileId");
      String microsoftAccessToken = (String) LauncherConfig.lookup.get("microsoftAccessToken");
      boolean accessTokenMatched = LauncherUtils.jwtPattern.matcher(microsoftAccessToken).matches();
      if (!accessTokenMatched) {
        MicrosoftAuthUtils.executeMicrosoftAuthWorker(MicrosoftAuthUtils.clientId);
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
      this.signinButton.setText(bundle.getString("yap.signinButton.signing_in"));

      if (LauncherUtils.isOutdated()) {
        LauncherUtils.swapContainers(
            this.getParent(), new LauncherNoNetworkPanel("lnnp.errorLabel.signin_outdated"));
        return;
      }

      String username = this.usernameField.getText();
      String password = new String(this.passwordField.getPassword());
      boolean rememberPasswordChecked = this.rememberPasswordCheckBox.isSelected();

      Object mojangUsername = LauncherConfig.lookup.get("mojangUsername");
      Object mojangPassword = LauncherConfig.lookup.get("mojangPassword");
      String mojangProfileName = (String) LauncherConfig.lookup.get("mojangProfileName");
      String mojangProfileId = (String) LauncherConfig.lookup.get("mojangProfileId");
      String mojangAccessToken = (String) LauncherConfig.lookup.get("mojangAccessToken");
      String mojangClientToken = (String) LauncherConfig.lookup.get("mojangClientToken");

      boolean usernameChanged = Objects.equals(mojangUsername, username);
      boolean passwordChanged = Objects.equals(mojangPassword, password) && !password.isEmpty();
      boolean accessTokenMatched = LauncherUtils.jwtPattern.matcher(mojangAccessToken).matches();
      boolean clientTokenMatched = LauncherUtils.uuidPattern.matcher(mojangClientToken).matches();
      if ((!usernameChanged || !passwordChanged) || (!accessTokenMatched || !clientTokenMatched)) {
        YggdrasilAuthUtils.executeYggdrasilAuthWorker(
            username, password, mojangClientToken, rememberPasswordChecked);
        return;
      }

      Launcher.launchMinecraft(mojangProfileName, mojangAccessToken, mojangProfileId);
    }
  }
}
