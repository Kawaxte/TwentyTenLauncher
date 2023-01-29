package ee.twentyten.ui.panel;

import ee.twentyten.core.swing.JBorderPanel;
import ee.twentyten.util.LauncherManager;
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
import lombok.Setter;

@Getter
@Setter
public class LoginLauncherPanel extends JBorderPanel {

  private JLabel errorLabel;
  private JLabel usernameLabel;
  private JTextField usernameField;
  private JLabel passwordLabel;
  private JPasswordField passwordField;
  private JButton optionsButton;
  private JCheckBox rememberPasswordCheckBox;
  private JLabel linkLabel;
  private String linkUrls;
  private boolean outdated;
  private JButton loginButton;

  public LoginLauncherPanel() {
    super(new BorderLayout(0, 8), true);

    this.setBackground(Color.GRAY);

    this.initComponents();
  }

  private void initComponents() {
    this.linkUrls =
        this.outdated ? LauncherManager.LATEST_RELEASE_URL : LauncherManager.ACCOUNT_SIGNUP_URL;
    this.outdated = LauncherManager.isOutdated();

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
    this.usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
    this.passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
    this.optionsButton = new JButton("Options");
    middlePanel1.add(this.usernameLabel, 0);
    middlePanel1.add(this.passwordLabel, 1);
    middlePanel1.add(this.optionsButton, 2);
    middlePanel1.setBackground(Color.GRAY);
    this.add(middlePanel1, BorderLayout.WEST);

    JPanel middlePanel2 = new JPanel(gl, true);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.rememberPasswordCheckBox = new JCheckBox("Remember Password");
    this.rememberPasswordCheckBox.setContentAreaFilled(false);
    middlePanel2.add(this.usernameField, 0);
    middlePanel2.add(this.passwordField, 1);
    middlePanel2.add(this.rememberPasswordCheckBox, 2);
    middlePanel2.setBackground(Color.GRAY);
    this.add(middlePanel2, BorderLayout.CENTER);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.linkLabel = new JLabel(this.outdated ? String.format(
        "<html><a href='%s'>You need to update the launcher!</a></html>",
        LauncherManager.LATEST_RELEASE_URL)
        : String.format("<html><a href='%s'>Need account?</a></html>",
            LauncherManager.ACCOUNT_SIGNUP_URL), SwingConstants.LEFT);
    this.linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.linkLabel.setForeground(Color.BLUE);
    this.loginButton = new JButton("Login");
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
    bottomPanel.setBackground(Color.GRAY);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
