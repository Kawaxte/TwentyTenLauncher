package ee.twentyten.ui.launcher;

import com.mojang.util.YggdrasilHelper;
import ee.twentyten.config.LauncherConfig;
import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.LauncherOptionPane;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.util.FileHelper;
import ee.twentyten.util.LauncherVersionHelper;
import ee.twentyten.util.LoggerHelper;
import ee.twentyten.util.OptionsHelper;
import ee.twentyten.util.RuntimeHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import lombok.Getter;
import org.json.JSONObject;

public class LauncherLoginPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static final long CACHE_EXPIRATION_TIME;

  static {
    CACHE_EXPIRATION_TIME = 86400000L;
  }

  @Getter
  private final String latestReleaseUrl;
  @Getter
  private final String accountSignupUrl;
  @Getter
  private final String errorLabelFailedText;
  @Getter
  private final String errorLabelOutdatedText;
  @Getter
  private final String errorLabelConnectionText;
  private final String usernameLabelText;
  private final String passwordLabelText;
  private final String optionsButtonText;
  private final String rememberPasswordCheckBoxText;
  private final String linkLabelSignupText;
  private final String linkLabelOutdatedText;
  private final String optionsTitleText;
  private final String loginButtonText;
  boolean isOutdated;
  @Getter
  private String linkUrls;
  private boolean isOutdatedChecked = false;
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
    this.linkUrls = accountSignupUrl;

    this.errorLabelFailedText = LauncherLanguage.getString(
        "lp.label.errorLabel.failed");
    this.errorLabelOutdatedText = LauncherLanguage.getString(
        "lp.label.errorLabel.outdated");
    this.errorLabelConnectionText = LauncherLanguage.getString(
        "lp.label.errorLabel.connection");
    this.usernameLabelText = LauncherLanguage.getString(
        "llp.label.usernameLabel");
    this.passwordLabelText = LauncherLanguage.getString(
        "llp.label.passwordLabel");
    this.optionsButtonText = LauncherLanguage.getString(
        "llp.button.optionsButton");
    this.rememberPasswordCheckBoxText = LauncherLanguage.getString(
        "llp.checkbox.rememberPasswordCheckBox");
    this.linkLabelSignupText = LauncherLanguage.getString(
        "llp.label.linkLabel.register");
    this.linkLabelOutdatedText = LauncherLanguage.getString(
        "llp.label.linkLabel.outdated");
    this.optionsTitleText = LauncherLanguage.getString("od.string.title");
    this.loginButtonText = LauncherLanguage.getString("llp.button.loginButton");
  }

  public LauncherLoginPanel() {
    super(new BorderLayout(0, 8), true);

    this.createTopPanel();
    this.createMiddlePanel();
    this.createBottomPanel();

    this.optionsButton.addActionListener(this);
    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent evt) {
        RuntimeHelper.openBrowser(LauncherLoginPanel.this.linkUrls);
      }
    };
    this.linkLabel.addMouseListener(adapter);
    this.loginButton.addActionListener(this);
  }

  static boolean isVersionSelected() {
    boolean isVersionSelected = !LauncherConfig.instance.getUsingBeta()
        && !LauncherConfig.instance.getUsingAlpha()
        && !LauncherConfig.instance.getUsingInfdev();
    if (isVersionSelected) {
      LauncherOptionPane.instance.showVersionError();
      return true;
    }
    return false;
  }

  private boolean isLauncherOutdated(LauncherPanel panel) {
    if (!this.isOutdatedChecked) {
      this.isOutdated = LauncherVersionHelper.isLauncherOutdated();
      this.isOutdatedChecked = true;
    }
    if (this.isOutdated) {
      panel.addNoNetworkPanel(this.errorLabelOutdatedText);

      this.linkUrls = this.latestReleaseUrl;

      String outdatedString = String.format("<html><a href='%s'>%s</a></html>",
          this.latestReleaseUrl, this.linkLabelOutdatedText);
      this.linkLabel.setText(outdatedString);
      return true;
    }
    return false;
  }

  private boolean isLoggedIn(LauncherPanel panel, String username,
      String password) {
    JSONObject result = YggdrasilHelper.authenticate(username, password, true,
        this);
    if (result.has("error")) {
      panel.addNoNetworkPanel(this.errorLabelFailedText);
      return true;
    }
    if (!result.has("selectedProfile")) {
      LauncherFrame.instance.launchMinecraft(false);
      return true;
    }
    return false;
  }

  private void showOptionsDialog() {
    OptionsDialog dialog = new OptionsDialog(this.optionsTitleText,
        LauncherFrame.instance);
    dialog.setVisible(true);
  }

  private void createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setBackground(this.getBackground());
    this.add(topPanel, BorderLayout.NORTH);

    Font errorLabelFont = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(errorLabelFont);
    this.errorLabel.setForeground(Color.RED.darker());
    this.add(this.errorLabel, BorderLayout.NORTH);
  }

  private void createMiddlePanel() {
    JPanel westernMiddlePanel = new JPanel(new GridLayout(3, 1, 0, 2), true);
    westernMiddlePanel.setBackground(this.getBackground());
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
    easternMiddlePanel.setBackground(this.getBackground());
    this.add(easternMiddlePanel, BorderLayout.CENTER);

    String savedUsername = LauncherConfig.instance.getUsername();
    this.usernameField = new JTextField(20);
    this.usernameField.setText(savedUsername);
    easternMiddlePanel.add(this.usernameField, 0);

    String savedPassword = LauncherConfig.instance.getPassword();
    this.passwordField = new JPasswordField(20);
    this.passwordField.setText(savedPassword);
    easternMiddlePanel.add(this.passwordField, 1);

    boolean isPasswordSaved = LauncherConfig.instance.getPasswordSaved();
    this.rememberPasswordCheckBox = new JCheckBox(
        this.rememberPasswordCheckBoxText);
    this.rememberPasswordCheckBox.setContentAreaFilled(false);
    this.rememberPasswordCheckBox.setSelected(isPasswordSaved);
    easternMiddlePanel.add(this.rememberPasswordCheckBox, 2);
  }

  private void createBottomPanel() {
    String registerString = String.format("<html><a href='%s'>%s</a></html>",
        this.accountSignupUrl, this.linkLabelSignupText);

    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.setBackground(this.getBackground());
    this.add(bottomPanel, BorderLayout.SOUTH);

    Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    this.linkLabel = new JLabel(registerString, SwingConstants.LEFT);
    this.linkLabel.setCursor(handCursor);
    this.linkLabel.setForeground(Color.BLUE);
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);

    this.loginButton = new JButton(this.loginButtonText);
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.optionsButton) {
      File versionsDirectory = new File(FileHelper.workingDirectory,
          "versions");
      if (!versionsDirectory.exists() && !versionsDirectory.mkdirs()) {
        LoggerHelper.logError("Failed to create versions directory", true);
        return;
      }

      File versionsFile = new File(versionsDirectory, "versions.json");
      if (!versionsFile.exists()) {
        long lastModified =
            System.currentTimeMillis() - versionsFile.lastModified();

        if (lastModified > CACHE_EXPIRATION_TIME) {
          FileHelper.downloadFile(OptionsHelper.VERSIONS_JSON_URL,
              versionsFile);
        }
      }

      this.showOptionsDialog();
    }
    if (src == this.loginButton) {
      LauncherPanel panel = LauncherFrame.instance.getPanel();
      /*
      if (this.loginPanel.isLauncherOutdated(pane)) {
        return;
      }
      */
      if (LauncherLoginPanel.isVersionSelected()) {
        return;
      }

      try {
        InetAddress address = InetAddress.getByName("www.minecraft.net");
        if (address != null) {
          String username = this.usernameField.getText();
          String password = new String(this.passwordField.getPassword());

          boolean hasLoginChanged =
              !username.equals(LauncherConfig.instance.getUsername())
                  || !password.equals(LauncherConfig.instance.getPassword());
          boolean isLoginEmpty = username.isEmpty() || password.isEmpty();
          if (hasLoginChanged || isLoginEmpty
              || LauncherFrame.isSessionExpired) {
            if (this.isLoggedIn(panel, username, password)) {
              return;
            }

            String profileName = LauncherConfig.instance.getProfileName();
            String sessionId = LauncherConfig.instance.getSessionId();
            LauncherFrame.instance.launchMinecraft(profileName, sessionId,
                true);
            return;
          }

          if (LauncherConfig.instance.getProfileName().isEmpty()) {
            LauncherFrame.instance.launchMinecraft(false);
            return;
          }
          if (LauncherConfig.instance.getRefreshToken().isEmpty()) {
            String profileName = LauncherConfig.instance.getProfileName();
            String sessionId = LauncherConfig.instance.getSessionId();
            LauncherFrame.instance.launchMinecraft(profileName, sessionId,
                true);
          }
        }
      } catch (UnknownHostException uhe) {
        panel.addNoNetworkPanel(this.errorLabelConnectionText);

        LoggerHelper.logError("Failed to get address", uhe, true);
      }
    }
  }
}
