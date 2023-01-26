package ee.twentyten.ui;

import ee.twentyten.core.swing.JBorderPanel;
import ee.twentyten.utils.VersionManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
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
public class LauncherLoginPanel extends JBorderPanel {

  private String linkUrls;
  private boolean outdated;

  private JLabel errorLabel;
  private JLabel usernameLabel;
  private JTextField usernameField;
  private JLabel passwordLabel;
  private JPasswordField passwordField;
  private JButton optionsButton;
  private JCheckBox rememberPasswordCheckBox;
  private JLabel linkLabel;
  private JButton loginButton;

  public LauncherLoginPanel() {
    super(new BorderLayout(0, 8), true);

    this.setBackground(Color.GRAY);

    this.initComponents();
  }

  private void initComponents() {
    this.linkUrls =
        this.outdated ? VersionManager.GITHUB_LATEST_URL : VersionManager.SIGNUP_LIVE_URL;
    this.outdated = VersionManager.isOutdated();

    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
    this.errorLabel.setForeground(Color.RED.darker());
    this.add(this.errorLabel, BorderLayout.NORTH);

    GridLayout gl = new GridLayout(3, 1, 0, 2);
    this.createMiddlePanel1(gl);
    this.createMiddlePanel2(gl);
    this.createBottomPanel();

    MouseAdapter ma = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent me) {
        Object source = me.getSource();
        if (source == linkLabel) {
          try {
            Desktop.getDesktop().browse(URI.create(linkUrls));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };
    this.linkLabel.addMouseListener(ma);
  }

  private void createMiddlePanel1(GridLayout gl) {
    JPanel middlePanel1 = new JPanel(gl);
    middlePanel1.setBackground(Color.GRAY);

    this.usernameLabel = new JLabel("Username:", SwingConstants.RIGHT);
    middlePanel1.add(this.usernameLabel, 0);

    this.passwordLabel = new JLabel("Password:", SwingConstants.RIGHT);
    middlePanel1.add(this.passwordLabel, 1);

    this.optionsButton = new JButton("Options");
    middlePanel1.add(this.optionsButton, 2);

    this.add(middlePanel1, BorderLayout.WEST);
  }

  private void createMiddlePanel2(GridLayout gl) {
    JPanel middlePanel2 = new JPanel(gl);
    middlePanel2.setBackground(Color.GRAY);

    this.usernameField = new JTextField(20);
    middlePanel2.add(this.usernameField, 0);

    this.passwordField = new JPasswordField(20);
    middlePanel2.add(this.passwordField, 1);

    this.rememberPasswordCheckBox = new JCheckBox("Remember Password");
    this.rememberPasswordCheckBox.setContentAreaFilled(false);
    middlePanel2.add(this.rememberPasswordCheckBox, 2);

    this.add(middlePanel2, BorderLayout.CENTER);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(Color.GRAY);

    this.linkLabel = new JLabel(this.outdated ? String.format(
        "<html><a href='%s'>You need to update the launcher!</a></html>",
        VersionManager.GITHUB_LATEST_URL)
        : String.format("<html><a href='%s'>Need account?</a></html>",
            VersionManager.SIGNUP_LIVE_URL), SwingConstants.LEFT);
    this.linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.linkLabel.setForeground(Color.BLUE);
    bottomPanel.add(this.linkLabel, BorderLayout.WEST);

    this.loginButton = new JButton("Login");
    bottomPanel.add(this.loginButton, BorderLayout.EAST);

    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
