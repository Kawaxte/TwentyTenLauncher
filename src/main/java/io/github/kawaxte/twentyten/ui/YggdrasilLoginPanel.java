package io.github.kawaxte.twentyten.ui;

import io.github.kawaxte.twentyten.conf.AbstractLauncherConfigImpl;
import io.github.kawaxte.twentyten.lang.LauncherLanguage;
import io.github.kawaxte.twentyten.misc.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.misc.ui.CustomJPanel;
import io.github.kawaxte.twentyten.misc.ui.CustomJPasswordField;
import io.github.kawaxte.twentyten.misc.ui.CustomJTextField;
import io.github.kawaxte.twentyten.misc.ui.JHyperlink;
import io.github.kawaxte.twentyten.misc.ui.TransparentJButton;
import io.github.kawaxte.twentyten.misc.ui.TransparentJCheckBox;
import io.github.kawaxte.twentyten.ui.options.OptionsDialog;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.val;

public class YggdrasilLoginPanel extends CustomJPanel implements ActionListener {

  public static YggdrasilLoginPanel instance;
  private static final long serialVersionUID = 1L;
  private final TransparentJButton microsoftLoginButton;
  private final JLabel usernameLabel;
  private final JLabel passwordLabel;
  private final CustomJTextField usernameField;
  private final CustomJPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final JHyperlink linkLabel;
  private final TransparentJButton loginButton;

  {
    this.microsoftLoginButton = new TransparentJButton("ylp.microsoftLoginButton");
    this.usernameLabel = new JLabel("ylp.usernameLabel",
        SwingConstants.RIGHT);
    this.passwordLabel = new JLabel("ylp.passwordLabel",
        SwingConstants.RIGHT);
    this.usernameField = new CustomJTextField(20);
    this.passwordField = new CustomJPasswordField(20);
    this.optionsButton = new TransparentJButton("ylp.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("ylp.rememberPasswordCheckBox");
    this.linkLabel = new JHyperlink("ylp.linkLabel.signup",
        SwingConstants.LEFT);
    this.loginButton = new TransparentJButton("ylp.loginButton");
  }

  public YggdrasilLoginPanel() {
    super(true);

    YggdrasilLoginPanel.instance = this;
    this.setLayout(this.getGroupLayout());

    this.microsoftLoginButton.addActionListener(this);
    this.optionsButton.addActionListener(this);
    this.loginButton.addActionListener(this);

    val selectedLanguage = AbstractLauncherConfigImpl.INSTANCE.getSelectedLanguage();
    this.updateComponentKeyValues(Objects.nonNull(selectedLanguage)
        ? LauncherLanguage.getUtf8Bundle(selectedLanguage)
        : LauncherLanguage.getUtf8Bundle());
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(bundle,
        this.microsoftLoginButton,
        "ylp.microsoftLoginButton");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.usernameLabel,
        "ylp.usernameLabel");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.passwordLabel,
        "ylp.passwordLabel");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.optionsButton,
        "ylp.optionsButton");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.rememberPasswordCheckBox,
        "ylp.rememberPasswordCheckBox");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.linkLabel,
        "ylp.linkLabel.signup");
    LauncherUtils.updateComponentKeyValue(bundle,
        this.loginButton,
        "ylp.loginButton");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createParallelGroup(Alignment.CENTER)
            .addComponent(this.microsoftLoginButton,
                0,
                0,
                Short.MAX_VALUE)
            .addGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameLabel,
                        Alignment.TRAILING)
                    .addComponent(this.passwordLabel,
                        Alignment.TRAILING)
                    .addComponent(this.optionsButton,
                        Alignment.TRAILING))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameField)
                    .addComponent(this.passwordField)
                    .addComponent(this.rememberPasswordCheckBox)))
            .addGroup(groupLayout.createSequentialGroup()
                .addComponent(this.linkLabel)
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(this.loginButton)));
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.microsoftLoginButton)
            .addPreferredGap(ComponentPlacement.UNRELATED,
                GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.usernameLabel)
                .addComponent(this.usernameField))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.passwordLabel)
                .addComponent(this.passwordField))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.optionsButton)
                .addComponent(this.rememberPasswordCheckBox))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.linkLabel)
                .addComponent(this.loginButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    /*
    if (Objects.equals(source, this.microsoftLoginButton)) {
      LauncherUtils.addPanel(this.getParent(),
          new MicrosoftLoginPanel());
    }

     */
    if (Objects.equals(source, this.optionsButton)) {
      SwingUtilities.invokeLater(() -> {
        val optionsDialog = new OptionsDialog(SwingUtilities.getWindowAncestor(this));
        optionsDialog.setVisible(true);
      });
    }
    /*
    if (Objects.equals(source, this.loginButton)) {
      LauncherUtils.addPanel(this.getParent(),
          new LauncherOfflinePanel());
    }
     */
  }
}
