package com.github.kawaxte.ttl.launcher.ui;

import com.github.kawaxte.ttl.custom.ui.CustomJLabel;
import com.github.kawaxte.ttl.custom.ui.CustomJPanel;
import com.github.kawaxte.ttl.custom.ui.CustomJPasswordField;
import com.github.kawaxte.ttl.custom.ui.CustomJTextField;
import com.github.kawaxte.ttl.custom.ui.TransparentJButton;
import com.github.kawaxte.ttl.custom.ui.TransparentJCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.Setter;

public class YggdrasilLoginPanel extends CustomJPanel {

  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  public static YggdrasilLoginPanel ylpInstance;
  private final CustomJLabel errorLabel;
  private final CustomJLabel usernameLabel;
  private final CustomJLabel passwordLabel;
  private final CustomJTextField usernameField;
  private final CustomJPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final CustomJLabel linkLabel;
  private final TransparentJButton loginButton;

  {
    this.errorLabel = new CustomJLabel("\u00A0", SwingConstants.HORIZONTAL, CustomJLabel.ERROR);
    this.usernameLabel = new CustomJLabel("Username:", SwingConstants.RIGHT);
    this.passwordLabel = new CustomJLabel("Password:", SwingConstants.RIGHT);

    this.usernameField = new CustomJTextField(20);
    this.passwordField = new CustomJPasswordField(20);
    this.optionsButton = new TransparentJButton("Options");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("Remember password");
    this.linkLabel = new CustomJLabel("Need account?", SwingConstants.LEFT, CustomJLabel.HYPERLINK);
    this.loginButton = new TransparentJButton("Login");
  }

  public YggdrasilLoginPanel() {
    super(true);

    YggdrasilLoginPanel.setYlpInstance(this);

    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createParallelGroup(Alignment.LEADING)
            .addComponent(this.errorLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
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
            .addComponent(this.errorLabel)
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
