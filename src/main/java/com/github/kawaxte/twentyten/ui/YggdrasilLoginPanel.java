package com.github.kawaxte.twentyten.ui;

import com.github.kawaxte.twentyten.misc.ui.CustomJLabel;
import com.github.kawaxte.twentyten.misc.ui.CustomJPanel;
import com.github.kawaxte.twentyten.misc.ui.CustomJPasswordField;
import com.github.kawaxte.twentyten.misc.ui.CustomJTextField;
import com.github.kawaxte.twentyten.misc.ui.TransparentJButton;
import com.github.kawaxte.twentyten.misc.ui.TransparentJCheckBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import lombok.var;

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
    this.microsoftLoginButton = new TransparentJButton("ylp.microsoftButton");
    this.usernameLabel = new CustomJLabel("ylp.usernameLabel", SwingConstants.RIGHT);
    this.passwordLabel = new CustomJLabel("ylp.passwordLabel", SwingConstants.RIGHT);
    this.usernameField = new CustomJTextField(20);
    this.passwordField = new CustomJPasswordField(20);
    this.optionsButton = new TransparentJButton("ylp.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox("ylp.rememberPasswordCheckBox");
    this.linkLabel = new CustomJLabel("ylp.linkLabel", SwingConstants.LEFT, CustomJLabel.HYPERLINK);
    this.loginButton = new TransparentJButton("ylp.loginButton");
  }

  public YggdrasilLoginPanel() {
    super(true);

    var groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createParallelGroup(Alignment.CENTER)
            .addComponent(this.microsoftLoginButton, GroupLayout.DEFAULT_SIZE,
                GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameLabel, Alignment.TRAILING)
                    .addComponent(this.passwordLabel, Alignment.TRAILING)
                    .addComponent(this.optionsButton, Alignment.TRAILING))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(this.usernameField)
                    .addComponent(this.passwordField)
                    .addComponent(this.rememberPasswordCheckBox)))
            .addGroup(groupLayout.createSequentialGroup()
                .addComponent(this.linkLabel)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(this.loginButton))
    );
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.microsoftLoginButton)
            .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE,
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
                .addComponent(this.loginButton))
    );
    this.setLayout(groupLayout);
  }
}
