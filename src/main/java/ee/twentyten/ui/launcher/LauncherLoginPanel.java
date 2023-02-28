package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJLabel;
import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.custom.TransparentJButton;
import ee.twentyten.custom.TransparentJCheckBox;
import ee.twentyten.custom.TransparentPanelUI;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.log.ELogger;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.ui.OptionsDialog;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import ee.twentyten.util.LoggerUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
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
  public static LauncherLoginPanel instance;
  private final JLabel errorLabel;
  private final JLabel usernameLabel;
  private final JLabel passwordLabel;
  private final JTextField usernameField;
  private final JPasswordField passwordField;
  private final TransparentJButton optionsButton;
  private final TransparentJCheckBox rememberPasswordCheckBox;
  private final CustomJLabel linkLabel;
  private final TransparentJButton loginButton;

  {
    this.errorLabel = new JLabel("\u00A0", JLabel.CENTER);
    this.usernameLabel = new JLabel(LanguageUtils.usernameLabelKey, JLabel.RIGHT);
    this.passwordLabel = new JLabel(LanguageUtils.passwordLabelKey, JLabel.RIGHT);
    this.usernameField = new JTextField(20);
    this.passwordField = new JPasswordField(20);
    this.optionsButton = new TransparentJButton(LanguageUtils.optionsButtonKey);
    this.rememberPasswordCheckBox = new TransparentJCheckBox(
        LanguageUtils.rememberPasswordCheckBoxKey);
    this.linkLabel = new CustomJLabel(LanguageUtils.needAccountKey, JLabel.CENTER);
    this.loginButton = new TransparentJButton(LanguageUtils.loginButtonKey);

    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        Desktop d = Desktop.getDesktop();
        if (d.isSupported(Action.BROWSE)) {
          try {
            d.browse(LauncherUtils.isOutdated ? LauncherUtils.latestReleaseUrl.toURI()
                : LauncherUtils.registrationUrl.toURI());
          } catch (IOException ioe) {
            LoggerUtils.log("Failed to launch browser", ioe, ELogger.ERROR);
          } catch (URISyntaxException urise) {
            LoggerUtils.log("Failed to resolve URI", urise, ELogger.ERROR);
          }
        }
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
    LanguageUtils.setTextToComponent(bundle, this.usernameLabel, LanguageUtils.usernameLabelKey);
    LanguageUtils.setTextToComponent(bundle, this.passwordLabel, LanguageUtils.passwordLabelKey);
    LanguageUtils.setTextToComponent(bundle, this.optionsButton, LanguageUtils.optionsButtonKey);
    LanguageUtils.setTextToComponent(bundle, this.rememberPasswordCheckBox,
        LanguageUtils.rememberPasswordCheckBoxKey);
    LanguageUtils.setTextToComponent(bundle, this.linkLabel,
        LauncherUtils.isOutdated ? LanguageUtils.updateLauncherKey : LanguageUtils.needAccountKey);
    LanguageUtils.setTextToComponent(bundle, this.loginButton, LanguageUtils.loginButtonKey);
  }

  void buildTopPanel() {
    Font errorFont = new Font(Font.SANS_SERIF, Font.ITALIC, 16);
    this.errorLabel.setFont(errorFont);
    this.errorLabel.setForeground(Color.RED.darker());

    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setUI(new TransparentPanelUI());
    topPanel.add(this.errorLabel, BorderLayout.NORTH);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel1 = new JPanel(new GridLayout(3, 1, 0, 2), true);
    middlePanel1.setUI(new TransparentPanelUI());
    middlePanel1.add(this.usernameLabel, 0);
    middlePanel1.add(this.passwordLabel, 1);
    middlePanel1.add(this.optionsButton, 2);
    this.add(middlePanel1, BorderLayout.WEST);

    JPanel middlePanel2 = new JPanel(new GridLayout(3, 1, 0, 2), true);
    middlePanel2.setUI(new TransparentPanelUI());
    middlePanel2.add(this.usernameField, 0);
    middlePanel2.add(this.passwordField, 1);
    middlePanel2.add(this.rememberPasswordCheckBox, 2);
    this.add(middlePanel2, BorderLayout.CENTER);
  }

  private void buildBottomPanel() {
    if (LauncherUtils.isOutdated) {
      this.linkLabel.setText(LanguageUtils.updateLauncherKey);
    }

    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    bottomPanel.setUI(new TransparentPanelUI());
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);
    bottomPanel.add(this.loginButton, BorderLayout.EAST);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.optionsButton) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          new OptionsDialog(LauncherFrame.getInstance(), true);
        }
      });
    }
    if (source == this.loginButton) {
      if (LauncherUtils.isLauncherOutdated()) {
        LauncherPanel.getInstance().showNoNetworkPanel(
            LanguageUtils.getString(LanguageUtils.getBundle(), LanguageUtils.outdatedLauncherKey));
        return;
      }
      System.out.println("Login button pressed");
    }
  }
}
