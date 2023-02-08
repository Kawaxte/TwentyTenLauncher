package ee.twentyten.ui.launcher;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.util.LauncherHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import lombok.Getter;

public class LauncherLoginPanel extends CustomJPanel {

  private static final long serialVersionUID = 1L;
  private final String latestReleaseUrl;
  private final String accountSignupUrl;
  @Getter
  private JLabel errorLabel;
  @Getter
  private String linkUrls;
  private boolean outdated;
  @Getter
  private JTextField usernameField;
  @Getter
  private JPasswordField passwordField;
  @Getter
  private JButton optionsButton;
  @Getter
  private JCheckBox rememberPasswordCheckBox;
  @Getter
  private JLabel linkLabel;
  @Getter
  private JButton loginButton;

  {
    latestReleaseUrl = "https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest";
    accountSignupUrl = "https://signup.live.com/signup?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d&client_id=00000000402b5328&lic=1";
  }

  public LauncherLoginPanel() {
    super(new BorderLayout(0, 8), true);

    this.setBackground(Color.GRAY);

    this.initComponents();
  }

  private void initComponents() {
    this.linkUrls = this.outdated ? latestReleaseUrl : accountSignupUrl;
    this.outdated = LauncherHelper.isOutdated();

    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
    this.errorLabel.setForeground(Color.RED.darker());
    this.add(this.errorLabel, BorderLayout.NORTH);

    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    GridLayout gl = new GridLayout(3, 1, 0, 2);

    JPanel middlePanel1 = new JPanel(gl, true);
    middlePanel1.setBackground(Color.GRAY);
    this.add(middlePanel1, BorderLayout.WEST);

    JLabel usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
    middlePanel1.add(usernameLabel, 0);

    JLabel passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
    middlePanel1.add(passwordLabel, 1);

    this.optionsButton = new JButton("Options");
    middlePanel1.add(this.optionsButton, 2);

    JPanel middlePanel2 = new JPanel(gl, true);
    middlePanel2.setBackground(Color.GRAY);
    this.add(middlePanel2, BorderLayout.CENTER);

    this.usernameField = new JTextField(20);
    this.usernameField.setText(LauncherConfig.instance.getUsername());
    middlePanel2.add(this.usernameField, 0);

    this.passwordField = new JPasswordField(20);
    this.passwordField.setText(LauncherConfig.instance.getPassword());
    middlePanel2.add(this.passwordField, 1);

    this.rememberPasswordCheckBox = new JCheckBox("Remember Password");
    this.rememberPasswordCheckBox.setContentAreaFilled(false);
    this.rememberPasswordCheckBox.setSelected(LauncherConfig.instance.getPasswordSaved());
    middlePanel2.add(this.rememberPasswordCheckBox, 2);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.setBackground(Color.GRAY);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.linkLabel = new JLabel(this.outdated ? String.format(
        "<html><a href='%s'>You need to update the launcher!</a></html>", latestReleaseUrl)
        : String.format("<html><a href='%s'>Need account?</a></html>", accountSignupUrl),
        SwingConstants.LEFT);
    this.linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.linkLabel.setForeground(Color.BLUE);
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);

    this.loginButton = new JButton("Login");
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
  }
}
