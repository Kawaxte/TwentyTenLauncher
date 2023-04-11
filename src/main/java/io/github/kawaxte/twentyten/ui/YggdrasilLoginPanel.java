package io.github.kawaxte.twentyten.ui;

import io.github.kawaxte.twentyten.misc.ui.CustomJLabel;
import io.github.kawaxte.twentyten.misc.ui.CustomJPanel;
import io.github.kawaxte.twentyten.misc.ui.CustomJPasswordField;
import io.github.kawaxte.twentyten.misc.ui.CustomJTextField;
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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lombok.val;

public class YggdrasilLoginPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final TransparentJButton microsoftLoginButton;
  private final CustomJLabel emailLabel;
  private final CustomJLabel passwordLabel;
  private final CustomJTextField usernameField;
  private final CustomJPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final CustomJLabel linkLabel;
  private final TransparentJButton loginButton;

  {
    this.microsoftLoginButton = new TransparentJButton("ylp.microsoftButton");
    this.emailLabel = new CustomJLabel("ylp.usernameLabel",
        SwingConstants.RIGHT);
    this.passwordLabel = new CustomJLabel("ylp.passwordLabel",
        SwingConstants.RIGHT);
    this.usernameField = new CustomJTextField(20);
    this.passwordField = new CustomJPasswordField(20);
    this.optionsButton = new TransparentJButton("ylp.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("ylp.rememberPasswordCheckBox");
    this.linkLabel = new CustomJLabel("ylp.linkLabel.signup",
        SwingConstants.LEFT,
        CustomJLabel.HYPERLINK);
    this.loginButton = new TransparentJButton("ylp.loginButton");
  }

  public YggdrasilLoginPanel() {
    super(true);

    this.setLayout(this.getGroupLayout());

    this.microsoftLoginButton.addActionListener(this);
    this.optionsButton.addActionListener(this);
    this.loginButton.addActionListener(this);

    this.updateComponentKeyValues();
  }

  private void updateComponentKeyValues() {
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.microsoftLoginButton,
        this.microsoftLoginButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.emailLabel,
        this.emailLabel.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.passwordLabel,
        this.passwordLabel.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.optionsButton,
        this.optionsButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.rememberPasswordCheckBox,
        this.rememberPasswordCheckBox.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.linkLabel,
        this.linkLabel.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.loginButton,
        this.loginButton.getText());
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
                    .addComponent(this.emailLabel,
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
                .addComponent(this.emailLabel)
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
