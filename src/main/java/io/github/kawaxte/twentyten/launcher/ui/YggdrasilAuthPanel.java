package io.github.kawaxte.twentyten.launcher.ui;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.CONFIG;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.LANGUAGE;
import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.JWT_PATTERN;
import static io.github.kawaxte.twentyten.launcher.util.LauncherUtils.UUID_PATTERN;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.options.OptionsDialog;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import io.github.kawaxte.twentyten.launcher.util.YggdrasilAuthUtils;
import io.github.kawaxte.twentyten.ui.CustomJPanel;
import io.github.kawaxte.twentyten.ui.JHyperlink;
import io.github.kawaxte.twentyten.ui.TransparentJButton;
import io.github.kawaxte.twentyten.ui.TransparentJCheckBox;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.val;

public class YggdrasilAuthPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  public static YggdrasilAuthPanel instance;
  private final TransparentJButton microsoftSigninButton;
  @Getter private final JLabel usernameLabel;
  @Getter private final JLabel passwordLabel;
  private final JTextField usernameField;
  private final JPasswordField passwordField;
  private final TransparentJButton optionsButton;
  @Getter private final TransparentJCheckBox rememberPasswordCheckBox;
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
            LauncherUtils.outdated != null && LauncherUtils.outdated
                ? "ylp.linkLabel.update"
                : "ylp.linkLabel.signup",
            SwingConstants.LEFT);
    this.signinButton = new TransparentJButton("ylp.signinButton");
  }

  public YggdrasilAuthPanel() {
    super(true);

    YggdrasilAuthPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    val mojangUsername = CONFIG.getMojangUsername();
    val mojangPassword = CONFIG.getMojangPassword();
    val mojangRememberPasswordChecked = CONFIG.isMojangRememberPasswordChecked();
    if (Objects.nonNull(mojangUsername)) {
      this.usernameField.setText(mojangUsername);
    }
    if (Objects.nonNull(mojangPassword)) {
      this.passwordField.setText(mojangPassword);
    }
    this.rememberPasswordCheckBox.setSelected(mojangRememberPasswordChecked);

    this.microsoftSigninButton.addActionListener(this);
    this.optionsButton.addActionListener(this);
    this.signinButton.addActionListener(this);

    val selectedLanguage = CONFIG.getSelectedLanguage();
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : LANGUAGE.getBundle());
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
      LauncherUtils.addPanel(
          this.getParent(),
          LauncherUtils.isOutdated()
              ? new LauncherOfflinePanel("lop.errorLabel.signin_outdated")
              : new MicrosoftAuthPanel());
    }
    if (Objects.equals(source, this.optionsButton)) {
      SwingUtilities.invokeLater(
          () -> {
            val optionsDialog = new OptionsDialog(SwingUtilities.getWindowAncestor(this));
            optionsDialog.setVisible(true);
          });
    }
    if (Objects.equals(source, this.signinButton)) {
      if (LauncherUtils.isOutdated()) {
        LauncherUtils.addPanel(
            this.getParent(), new LauncherOfflinePanel("lop.errorLabel.signin_outdated"));
        return;
      }

      val username = this.usernameField.getText();
      val password = new String(this.passwordField.getPassword());
      val rememberPasswordChecked = this.rememberPasswordCheckBox.isSelected();

      val mojangUsername = CONFIG.getMojangUsername();
      val mojangPassword = CONFIG.getMojangPassword();
      val mojangRememberPasswordChecked = CONFIG.isMojangRememberPasswordChecked();
      val mojangAccessToken = CONFIG.getMojangAccessToken();
      val mojangClientToken = CONFIG.getMojangClientToken();

      val usernameEquals = Objects.equals(mojangUsername, username);
      val passwordEquals = Objects.equals(mojangPassword, password);
      val rememberPasswordCheckedEquals =
          Objects.equals(mojangRememberPasswordChecked, rememberPasswordChecked);
      val accessTokenMatches = JWT_PATTERN.matcher(mojangAccessToken).matches();
      val clientTokenMatches = UUID_PATTERN.matcher(mojangClientToken).matches();

      if ((Objects.isNull(mojangUsername) || Objects.isNull(mojangPassword))
          || (!usernameEquals || !passwordEquals || !rememberPasswordCheckedEquals)
          || (!accessTokenMatches || !clientTokenMatches)) {
        YggdrasilAuthUtils.authenticate(username, password, mojangClientToken);
      } else {
        // TODO: launch minecraft instance
        System.out.println("we can sign in!");
      }
    }
  }
}
