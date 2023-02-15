package ee.twentyten.ui.launcher;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.util.LauncherVersionHelper;
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
  private final String usernameLabelText;
  private final String passwordLabelText;
  private final String optionsButtonText;
  private final String rememberPasswordCheckBoxText;
  private final String linkLabelText;
  private final String linkLabelOutdatedText;
  private final String loginButtonText;
  @Getter
  private boolean isOutdated;
  @Getter
  private String linkUrls;
  @Getter
  private JLabel errorLabel;
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
    this.latestReleaseUrl =
        "https://github.com/" + "sojlabjoi/" + "AlphacraftLauncher/"
            + "releases/" + "latest";
    this.accountSignupUrl = "https://signup.live.com/" + "signup?"
        + "cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d"
        + "&client_id=00000000402b5328" + "&lic=1";

    this.isOutdated = LauncherVersionHelper.isLauncherOutdated();

    this.usernameLabelText = LauncherLanguage.getString(
        "llp.label.usernameLabel");
    this.passwordLabelText = LauncherLanguage.getString(
        "llp.label.passwordLabel");
    this.optionsButtonText = LauncherLanguage.getString(
        "llp.button.optionsButton");
    this.rememberPasswordCheckBoxText = LauncherLanguage.getString(
        "llp.checkbox.rememberPasswordCheckBox");
    this.linkLabelText = LauncherLanguage.getString(
        "llp.label.linkLabel.register");
    this.linkLabelOutdatedText = LauncherLanguage.getString(
        "llp.label.linkLabel.outdated");
    this.loginButtonText = LauncherLanguage.getString("llp.button.loginButton");
  }

  public LauncherLoginPanel() {
    super(new BorderLayout(0, 8), true);

    this.createTopPanel();
    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private String setOutdated(boolean isOutdated) {
    this.isOutdated = isOutdated;
    this.linkUrls = this.isOutdated ? latestReleaseUrl : accountSignupUrl;

    String registerString = String.format("<html><a href='%s'>%s</a></html>",
        this.accountSignupUrl, this.linkLabelText);
    String outdatedString = String.format("<html><a href='%s'>%s</a></html>",
        this.latestReleaseUrl, this.linkLabelOutdatedText);
    return this.isOutdated ? outdatedString : registerString;
  }

  private void createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setBackground(Color.GRAY);
    this.add(topPanel, BorderLayout.NORTH);

    Font errorLabelFont = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(errorLabelFont);

    this.errorLabel.setForeground(Color.RED.darker());
    this.add(this.errorLabel, BorderLayout.NORTH);
  }

  private void createMiddlePanel() {
    JPanel westernMiddlePanel = new JPanel(new GridLayout(3, 1, 0, 2), true);
    westernMiddlePanel.setBackground(Color.GRAY);
    this.add(westernMiddlePanel, BorderLayout.WEST);

    JLabel usernameLabel = new JLabel(this.usernameLabelText,
        SwingConstants.RIGHT);
    westernMiddlePanel.add(usernameLabel, 0);

    JLabel passwordLabel = new JLabel(this.passwordLabelText,
        SwingConstants.RIGHT);
    westernMiddlePanel.add(passwordLabel, 1);

    this.optionsButton = new JButton(this.optionsButtonText);
    westernMiddlePanel.add(this.optionsButton, 2);

    JPanel easternMiddlePanel = new JPanel(new GridLayout(3, 1, 0, 2), true);
    easternMiddlePanel.setBackground(Color.GRAY);

    this.add(easternMiddlePanel, BorderLayout.CENTER);
    this.usernameField = new JTextField(20);

    String savedUsername = LauncherConfig.instance.getUsername();
    this.usernameField.setText(savedUsername);
    easternMiddlePanel.add(this.usernameField, 0);

    this.passwordField = new JPasswordField(20);
    String savedPassword = LauncherConfig.instance.getPassword();
    this.passwordField.setText(savedPassword);

    easternMiddlePanel.add(this.passwordField, 1);
    this.rememberPasswordCheckBox = new JCheckBox(
        this.rememberPasswordCheckBoxText);

    this.rememberPasswordCheckBox.setContentAreaFilled(false);
    boolean isPasswordSaved = LauncherConfig.instance.getPasswordSaved();
    this.rememberPasswordCheckBox.setSelected(isPasswordSaved);
    easternMiddlePanel.add(this.rememberPasswordCheckBox, 2);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.setBackground(Color.GRAY);
    this.add(bottomPanel, BorderLayout.SOUTH);

    String linkLabelString = this.setOutdated(this.isOutdated);
    this.linkLabel = new JLabel(linkLabelString, SwingConstants.LEFT);

    Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    this.linkLabel.setCursor(handCursor);
    this.linkLabel.setForeground(Color.BLUE);

    bottomPanel.add(this.linkLabel, BorderLayout.WEST);
    this.loginButton = new JButton(this.loginButtonText);
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
  }
}
