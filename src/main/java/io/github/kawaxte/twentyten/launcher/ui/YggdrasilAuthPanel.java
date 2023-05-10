package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
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
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.val;

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
    this.microsoftSigninButton = new TransparentJButton("ylp.microsoftSigninButton");
    this.usernameLabel = new JLabel("ylp.usernameLabel", SwingConstants.RIGHT);
    this.passwordLabel = new JLabel("ylp.passwordLabel", SwingConstants.RIGHT);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.optionsButton = new TransparentJButton("ylp.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("ylp.rememberPasswordCheckBox");
    this.linkLabel =
        new JHyperlink(
            Objects.nonNull(LauncherUtils.outdated) && LauncherUtils.outdated
                ? "ylp.linkLabel.update"
                : "ylp.linkLabel.signup",
            SwingConstants.LEFT);
    this.signinButton = new TransparentJButton("ylp.signinButton");
  }

  public YggdrasilAuthPanel() {
    super(true);

    YggdrasilAuthPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    val mojangUsername = LauncherConfig.lookup.get("mojangUsername");
    val mojangPassword = LauncherConfig.lookup.get("mojangPassword");
    val mojangRememberPasswordChecked = LauncherConfig.lookup.get("mojangRememberPasswordChecked");
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

    val selectedLanguage = LauncherConfig.lookup.get("selectedLanguage");
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguage.getUTF8Bundle((String) selectedLanguage)
            : LauncherLanguage.bundle);
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle, this.microsoftSigninButton, "ylp.microsoftSigninButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.usernameLabel, "ylp.usernameLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.passwordLabel, "ylp.passwordLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.optionsButton, "ylp.optionsButton");
    LauncherUtils.updateComponentKeyValue(
        bundle, this.rememberPasswordCheckBox, "ylp.rememberPasswordCheckBox");
    LauncherUtils.updateComponentKeyValue(
        bundle,
        this.linkLabel,
        LauncherUtils.outdated != null && LauncherUtils.outdated
            ? "ylp.linkLabel.update"
            : "ylp.linkLabel.signup");
    LauncherUtils.updateComponentKeyValue(bundle, this.signinButton, "ylp.signinButton");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createParallelGroup(Alignment.CENTER)
            .addComponent(this.microsoftSigninButton, 0, 0, Short.MAX_VALUE)
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addGroup(
                        groupLayout
                            .createParallelGroup(Alignment.LEADING)
                            .addComponent(this.usernameLabel, Alignment.TRAILING)
                            .addComponent(this.passwordLabel, Alignment.TRAILING)
                            .addComponent(this.optionsButton, Alignment.TRAILING))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(
                        groupLayout
                            .createParallelGroup(Alignment.LEADING)
                            .addComponent(this.usernameField)
                            .addComponent(this.passwordField)
                            .addComponent(this.rememberPasswordCheckBox)))
            .addGroup(
                groupLayout
                    .createSequentialGroup()
                    .addComponent(this.linkLabel)
                    .addPreferredGap(
                        ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.signinButton)));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.microsoftSigninButton)
            .addPreferredGap(
                ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.usernameLabel)
                    .addComponent(this.usernameField))
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.passwordLabel)
                    .addComponent(this.passwordField))
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.optionsButton)
                    .addComponent(this.rememberPasswordCheckBox))
            .addGroup(
                groupLayout
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(this.linkLabel)
                    .addComponent(this.signinButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.microsoftSigninButton)) {
      if (LauncherUtils.isOutdated()) {
        LauncherUtils.addComponentToContainer(
            this.getParent(), new LauncherOfflinePanel("lop.errorLabel.signin_outdated"));
        return;
      }

      val microsoftAccessToken = (String) LauncherConfig.lookup.get("microsoftAccessToken");
      val accessTokenMatched = LauncherUtils.jwtPattern.matcher(microsoftAccessToken).matches();
      if (!accessTokenMatched) {
        MicrosoftAuthUtils.executeMicrosoftAuthWorker(MicrosoftAuthUtils.clientId);
        return;
      }

      // TODO: Launch online instance without re-authentication
      JOptionPane.showMessageDialog(
          this, "You are already signed in.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    if (Objects.equals(source, this.optionsButton)) {
      SwingUtilities.invokeLater(
          () -> new OptionsDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));
    }
    if (Objects.equals(source, this.signinButton)) {
      if (LauncherUtils.isOutdated()) {
        LauncherUtils.addComponentToContainer(
            this.getParent(), new LauncherOfflinePanel("lop.errorLabel.signin_outdated"));
        return;
      }

      val username = this.usernameField.getText();
      val password = new String(this.passwordField.getPassword());
      val rememberPasswordChecked = this.rememberPasswordCheckBox.isSelected();

      val mojangUsername = LauncherConfig.lookup.get("mojangUsername");
      val mojangPassword = LauncherConfig.lookup.get("mojangPassword");
      val mojangAccessToken = (String) LauncherConfig.lookup.get("mojangAccessToken");
      val mojangClientToken = (String) LauncherConfig.lookup.get("mojangClientToken");

      val usernameChanged = Objects.equals(mojangUsername, username);
      val passwordChanged = Objects.equals(mojangPassword, password) && !password.isEmpty();
      val accessTokenMatched = LauncherUtils.jwtPattern.matcher(mojangAccessToken).matches();
      val clientTokenMatched = LauncherUtils.uuidPattern.matcher(mojangClientToken).matches();
      if ((!usernameChanged || !passwordChanged) || (!accessTokenMatched || !clientTokenMatched)) {
        YggdrasilAuthUtils.executeYggdrasilAuthWorker(
            username, password, mojangClientToken, rememberPasswordChecked);
        return;
      }

      // TODO: Launch online instance without re-authentication
      JOptionPane.showMessageDialog(
          this, "You are already signed in.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
  }
}
