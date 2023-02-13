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
import java.awt.LayoutManager;
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
    this.latestReleaseUrl = "https://github.com/"
        + "sojlabjoi/"
        + "AlphacraftLauncher/"
        + "releases/"
        + "latest";
    this.accountSignupUrl = "https://signup.live.com/"
        + "signup?"
        + "cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d"
        + "&client_id=00000000402b5328"
        + "&lic=1";

    this.isOutdated = LauncherVersionHelper.isLauncherOutdated();

    this.usernameLabelText = LauncherLanguage
        .getString("llp.label.usernameLabel");
    this.passwordLabelText = LauncherLanguage
        .getString("llp.label.passwordLabel");
    this.optionsButtonText = LauncherLanguage
        .getString("llp.button.optionsButton");
    this.rememberPasswordCheckBoxText = LauncherLanguage
        .getString("llp.checkbox.rememberPasswordCheckBox");
    this.linkLabelText = LauncherLanguage
        .getString("llp.label.linkLabel.register");
    this.linkLabelOutdatedText = LauncherLanguage
        .getString("llp.label.linkLabel.outdated");
    this.loginButtonText = LauncherLanguage
        .getString("llp.button.loginButton");
  }

  public LauncherLoginPanel(
      LayoutManager layout, boolean isDoubleBuffered
  ) {

    /* Call the constructor of the parent class. */
    super(layout, isDoubleBuffered);

    /* Specify the layout of the top and bottom panel. */
    BorderLayout borderLayout = new BorderLayout();

    /* Specify the layout of the middle panel. */
    GridLayout gridLayout = new GridLayout(3, 1);

    /* Call the method to create the top panel. */
    this.createTopPanel(borderLayout);

    /* Call the method to create the middle panel. */
    this.createMiddlePanel(gridLayout);

    /* Call the method to create the bottom panel. */
    this.createBottomPanel(borderLayout);
  }

  /**
   * Set the outdated status of the object.
   *
   * @param isOutdated A boolean indicating whether the object is outdated or
   *                   not.
   * @return A formatted HTML string containing a link to either the latest
   * release or the account signup page. The link text is either the link label
   * text or the link label outdated text, depending on the value of
   * `isOutdated`.
   */
  private String setOutdated(boolean isOutdated) {
    /* Set the outdated status of the object. */
    this.isOutdated = isOutdated;

    /* Set the link URL. */
    this.linkUrls = this.isOutdated
        ? latestReleaseUrl
        : accountSignupUrl;

    /* Set the link label text. */
    String registerString = String.format(
        "<html><a href='%s'>%s</a></html>",
        this.accountSignupUrl, this.linkLabelText);
    String outdatedString = String.format(
        "<html><a href='%s'>%s</a></html>",
        this.latestReleaseUrl, this.linkLabelOutdatedText);
    
    return this.isOutdated
        ? outdatedString
        : registerString;
  }

  /**
   * Creates the top panel of the login window with the given layout manager.
   * <p>
   * The top panel contains a label in the center to display error messages in a
   * red, italicized font.
   *
   * @param layout The layout manager to use for the top panel.
   */
  private void createTopPanel(
      LayoutManager layout
  ) {

    /* Create the top panel. */
    JPanel topPanel = new JPanel(layout, true);

    /* Set the background color of the top panel. */
    topPanel.setBackground(Color.GRAY);

    /* Add the top panel to the login panel. */
    this.add(topPanel, BorderLayout.NORTH);

    /* Create the error label. */
    this.errorLabel = new JLabel(
        "\u00A0",
        SwingConstants.CENTER
    );

    /* Set the font of the error label. */
    Font errorLabelFont = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
    this.errorLabel.setFont(errorLabelFont);

    /* Set the foreground color of the error label. */
    this.errorLabel.setForeground(Color.RED.darker());

    /* Add the error label to the top panel. */
    this.add(this.errorLabel, BorderLayout.NORTH);
  }

  /**
   * Creates the middle panel of the login window with given layout manager. The
   * panel is divided into two subpanels, one for the labels and one for the
   * text fields.
   * <p>
   * The western subpanel contains the labels "Username" and "Password". The
   * eastern subpanel contains the text fields for the username and password,
   * and a checkbox for the user to choose if they want to remember their
   * password.
   *
   * @param layout The layout manager to use for the middle panel and its
   *               subpanels.
   */
  private void createMiddlePanel(
      LayoutManager layout
  ) {

    /* Create the western subpanel. */
    JPanel westernMiddlePanel = new JPanel(layout, true);

    /* Set the background color of the middle panel. */
    westernMiddlePanel.setBackground(Color.GRAY);

    /* Add the western subpanel to the middle panel. */
    this.add(westernMiddlePanel, BorderLayout.WEST);

    /* Create the username label. */
    JLabel usernameLabel = new JLabel(
        this.usernameLabelText,
        SwingConstants.RIGHT
    );

    /* Add the username label to the western subpanel. */
    westernMiddlePanel.add(usernameLabel, 0);

    /* Create the password label. */
    JLabel passwordLabel = new JLabel(
        this.passwordLabelText,
        SwingConstants.RIGHT
    );

    /* Add the password label to the western subpanel. */
    westernMiddlePanel.add(passwordLabel, 1);

    /* Create the options button. */
    this.optionsButton = new JButton(this.optionsButtonText);

    /* Add the options button to the western subpanel. */
    westernMiddlePanel.add(this.optionsButton, 2);

    /* Create the eastern subpanel. */
    JPanel easternMiddlePanel = new JPanel(layout, true);

    /* Add the eastern subpanel to the middle panel. */
    easternMiddlePanel.setBackground(Color.GRAY);

    /* Add the eastern subpanel to the middle panel. */
    this.add(easternMiddlePanel, BorderLayout.CENTER);

    /* Create the username text field. */
    this.usernameField = new JTextField(20);

    /* Set the text of the username text field to the username stored in the
     * configuration file. */
    String savedUsername = LauncherConfig.instance.getUsername();
    this.usernameField.setText(savedUsername);

    /* Add the username text field to the eastern subpanel. */
    easternMiddlePanel.add(this.usernameField, 0);

    /* Create the password text field. */
    this.passwordField = new JPasswordField(20);

    /* Set the text of the password text field to the password stored in the
     * configuration file. */
    String savedPassword = LauncherConfig.instance.getPassword();
    this.passwordField.setText(savedPassword);

    /* Add the password text field to the eastern subpanel. */
    easternMiddlePanel.add(this.passwordField, 1);

    /* Create the remember password checkbox. */
    this.rememberPasswordCheckBox = new JCheckBox(
        this.rememberPasswordCheckBoxText
    );

    /* Remove the background of the checkbox. */
    this.rememberPasswordCheckBox.setContentAreaFilled(false);

    /* Set the state of the remember password checkbox to the state stored in
     * the configuration file. */
    boolean isPasswordSaved = LauncherConfig.instance.getPasswordSaved();
    this.rememberPasswordCheckBox.setSelected(isPasswordSaved);

    /* Add the remember password checkbox to the eastern subpanel. */
    easternMiddlePanel.add(this.rememberPasswordCheckBox, 2);
  }

  /**
   * Creates the bottom panel of the login window with the specified layout
   * manager.
   * <p>
   * The bottom panel is divided into two sub-panels, one on the left and one on
   * the right. The left sub-panel contains a label that displays a link. The
   * right sub-panel contains a button. The link label and the button are added
   * to the bottom panel using the specified layout manager.
   *
   * @param layout The layout manager to use for the bottom panel and its
   *               sub-panels.
   */
  private void createBottomPanel(
      LayoutManager layout
  ) {

    /* Create the bottom panel. */
    JPanel bottomPanel = new JPanel(layout, true);

    /* Set the background color of the bottom panel. */
    bottomPanel.setBackground(Color.GRAY);

    /* Add the bottom panel to the login window. */
    this.add(bottomPanel, BorderLayout.SOUTH);

    /* Create the link label. */
    String linkLabelString = this.setOutdated(this.isOutdated);
    this.linkLabel = new JLabel(
        linkLabelString,
        SwingConstants.LEFT
    );

    /* Set the cursor of the link label to a hand cursor. */
    Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    this.linkLabel.setCursor(handCursor);

    /* Set the foreground color of the link label to blue. */
    this.linkLabel.setForeground(Color.BLUE);

    /* Add the link label to the bottom panel. */
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);

    /* Create the login button. */
    this.loginButton = new JButton(this.loginButtonText);

    /* Add the login button to the bottom panel. */
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
  }
}
