package com.github.kawaxte.twentyten.ui;

import com.github.kawaxte.twentyten.custom.ui.CustomJLabel;
import com.github.kawaxte.twentyten.custom.ui.CustomJPanel;
import com.github.kawaxte.twentyten.custom.ui.CustomJPasswordField;
import com.github.kawaxte.twentyten.custom.ui.CustomJTextField;
import com.github.kawaxte.twentyten.custom.ui.TransparentJButton;
import com.github.kawaxte.twentyten.custom.ui.TransparentJCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class YggdrasilLoginPanel extends CustomJPanel {

  private static final long serialVersionUID = 1L;
  private final TransparentJButton microsoftLoginButton;
  private final CustomJLabel usernameLabel;
  private final CustomJLabel passwordLabel;
  private final CustomJTextField usernameField;
  private final CustomJPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final CustomJLabel linkLabel;
  private final TransparentJButton loginButton;

  {
    this.microsoftLoginButton = new TransparentJButton("Sign in with Microsoft");

    this.usernameLabel = new CustomJLabel("Username:", SwingConstants.RIGHT);
    this.passwordLabel = new CustomJLabel("Password:", SwingConstants.RIGHT);
    this.usernameField = new CustomJTextField(20);
    this.passwordField = new CustomJPasswordField(20);

    this.optionsButton = new TransparentJButton("Options");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("Remember password");

    this.linkLabel = new CustomJLabel("Need account?", SwingConstants.LEFT, CustomJLabel.HYPERLINK);
    this.loginButton = new TransparentJButton("Log in");
  }

  public YggdrasilLoginPanel() {
    super(true);

    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createParallelGroup(Alignment.CENTER)
            .addComponent(this.microsoftLoginButton, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameLabel, Alignment.TRAILING)
                    .addComponent(this.passwordLabel, Alignment.TRAILING)
                    .addComponent(this.optionsButton, Alignment.TRAILING))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(gl.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameField)
                    .addComponent(this.passwordField)
                    .addComponent(this.rememberPasswordCheckBox)))
            .addGroup(gl.createSequentialGroup()
                .addComponent(this.linkLabel)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(this.loginButton))
    );
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.microsoftLoginButton)
            .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.usernameLabel)
                .addComponent(this.usernameField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.passwordLabel)
                .addComponent(this.passwordField))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.optionsButton)
                .addComponent(this.rememberPasswordCheckBox))
            .addGroup(gl.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.linkLabel)
                .addComponent(this.loginButton))
    );
    this.setLayout(gl);
  }
}
