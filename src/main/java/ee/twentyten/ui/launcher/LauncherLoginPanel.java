package ee.twentyten.ui.launcher;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.ui.CustomJPanel;
import ee.twentyten.custom.ui.JHyperlink;
import ee.twentyten.custom.ui.TransparentJButton;
import ee.twentyten.custom.ui.TransparentJCheckBox;
import ee.twentyten.custom.ui.TransparentPanelUI;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.util.ConfigUtils;
import ee.twentyten.util.DiscordUtils;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.YggdrasilUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LauncherLoginPanel extends CustomJPanel implements ActionListener {

  @Getter
  @Setter
  private static LauncherLoginPanel instance;
  private final JLabel usernameLabel;
  private final JLabel passwordLabel;
  private final JTextField usernameField;
  private final JPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final JHyperlink linkLabel;
  private final TransparentJButton loginButton;

  {
    this.usernameLabel = new JLabel("llp.label.usernameLabel", JLabel.RIGHT);
    this.passwordLabel = new JLabel("llp.label.passwordLabel", JLabel.RIGHT);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.optionsButton = new TransparentJButton("llp.button.optionsButton");
    this.rememberPasswordCheckBox = new TransparentJCheckBox(
        "llp.checkbox.rememberPasswordCheckBox");
    this.linkLabel = new JHyperlink(LauncherUtils.isOutdated ? "llp.label.linkLabel.updateLauncher"
        : "llp.label.linkLabel.needAccount", JLabel.CENTER);
    this.loginButton = new TransparentJButton("llp.button.loginButton");

    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        LauncherUtils.browseDesktop(LauncherUtils.isOutdated ? LauncherUtils.latestReleaseUrl
            : LauncherUtils.registrationUrl);
      }
    };
    this.linkLabel.addMouseListener(adapter);
    this.optionsButton.addActionListener(this);
    this.loginButton.addActionListener(this);
  }

  public LauncherLoginPanel() {
    super(new BorderLayout(0, 10), true);

    LauncherLoginPanel.setInstance(this);
    this.buildTopPanel();
    this.buildMiddlePanel();
    this.buildBottomPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.usernameLabel, "llp.label.usernameLabel");
    LanguageUtils.setTextToComponent(bundle, this.passwordLabel, "llp.label.passwordLabel");
    LanguageUtils.setTextToComponent(bundle, this.optionsButton, "llp.button.optionsButton");
    LanguageUtils.setTextToComponent(bundle, this.rememberPasswordCheckBox,
        "llp.checkbox.rememberPasswordCheckBox");
    LanguageUtils.setTextToComponent(bundle, this.linkLabel,
        LauncherUtils.isOutdated ? "llp.label.linkLabel.updateLauncher"
            : "llp.label.linkLabel.needAccount");
    LanguageUtils.setTextToComponent(bundle, this.loginButton, "llp.button.loginButton");
  }

  void buildTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setUI(new TransparentPanelUI());
    topPanel.add(new JLabel("\u00A0"), BorderLayout.NORTH);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel1 = new JPanel(new GridLayout(3, 1, 0, 2), true);
    middlePanel1.setUI(new TransparentPanelUI());
    middlePanel1.add(this.usernameLabel, 0);
    middlePanel1.add(this.passwordLabel, 1);
    middlePanel1.add(this.optionsButton, 2);
    this.add(middlePanel1, BorderLayout.WEST);

    boolean isPasswordSaved = ConfigUtils.getInstance().isYggdrasilPasswordSaved();
    this.usernameField.setText(ConfigUtils.getInstance().getYggdrasilUsername());
    this.passwordField.setText(
        isPasswordSaved ? ConfigUtils.getInstance().getYggdrasilPassword() : "");
    this.rememberPasswordCheckBox.setSelected(ConfigUtils.getInstance().isYggdrasilPasswordSaved());

    JPanel middlePanel2 = new JPanel(new GridLayout(3, 1, 0, 2), true);
    middlePanel2.setUI(new TransparentPanelUI());
    middlePanel2.add(this.usernameField, 0);
    middlePanel2.add(this.passwordField, 1);
    middlePanel2.add(this.rememberPasswordCheckBox, 2);
    this.add(middlePanel2, BorderLayout.CENTER);
  }

  private void buildBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.setUI(new TransparentPanelUI());
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source.equals(this.optionsButton)) {
      DiscordUtils.updateRichPresence("Setting Options");

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          new OptionsDialog(LauncherFrame.getInstance(), true);
        }
      });
    }
    if (source.equals(this.loginButton)) {
      if (LauncherUtils.isNetworkNotAvailable("authserver.mojang.com")) {
        return;
      }
      if (LauncherUtils.isLauncherOutdated()) {
        LauncherUtils.addPanelWithErrorMessage(LauncherPanel.getInstance(),
            new LauncherNoNetworkPanel(), LanguageUtils.getString(LanguageUtils.getBundle(),
                "lp.label.errorLabel.outdatedLauncher"));
      } else {
        YggdrasilUtils.loginWithYggdrasil();
      }
    }
  }
}
