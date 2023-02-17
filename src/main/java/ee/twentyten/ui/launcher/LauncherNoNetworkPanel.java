package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;
import net.minecraft.MinecraftLauncher;

public class LauncherNoNetworkPanel extends CustomJPanel implements
    ActionListener {

  private static final long serialVersionUID = 1L;
  private final String playOnlineLabelText;
  private final String playOfflineButtonText;
  private final String tryAgainButtonText;
  @Getter
  private JLabel errorLabel;
  @Getter
  private JLabel playOnlineLabel;
  @Getter
  private JButton playOfflineButton;
  @Getter
  private JButton tryAgainButton;

  {
    this.playOnlineLabelText = LauncherLanguage.getString(
        "lop.label.playOnlineLabel");
    this.playOfflineButtonText = LauncherLanguage.getString(
        "lop.button.playOfflineButton");
    this.tryAgainButtonText = LauncherLanguage.getString(
        "lop.button.tryAgainButton");
  }

  public LauncherNoNetworkPanel() {
    super(new BorderLayout(0, 8), true);

    this.createTopPanel();
    this.createMiddlePanel();
    this.createBottomPanel();

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);
  }

  private void createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setBackground(this.getBackground());
    this.add(topPanel, BorderLayout.NORTH);

    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
    this.errorLabel.setForeground(Color.RED.darker());
    topPanel.add(this.errorLabel, BorderLayout.NORTH);
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.setBackground(this.getBackground());
    this.add(middlePanel, BorderLayout.CENTER);

    this.playOnlineLabel = new JLabel(this.playOnlineLabelText,
        SwingConstants.LEFT);
    if (!MinecraftLauncher.isMinecraftCached()) {
      middlePanel.add(this.playOnlineLabel, BorderLayout.SOUTH);
    }
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new GridLayout(1, 2), true);
    bottomPanel.setBackground(this.getBackground());
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.playOfflineButton = new JButton(this.playOfflineButtonText);
    this.playOfflineButton.setEnabled(MinecraftLauncher.isMinecraftCached());
    bottomPanel.add(this.playOfflineButton, BorderLayout.WEST);

    this.tryAgainButton = new JButton(this.tryAgainButtonText);
    bottomPanel.add(this.tryAgainButton, BorderLayout.EAST);
  }

  private void addLauncherPanelComponents(LauncherPanel panel,
      GridBagConstraints gbc) {
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets.top = 23;
    panel.add(panel.getLoginPanel(), gbc);

    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets.top = 0;
    panel.add(panel.getMicrosoftLoginButton(), gbc);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.playOfflineButton) {
      LauncherFrame.instance.launchMinecraft(false);
    }
    if (src == this.tryAgainButton) {
      LauncherPanel panel = LauncherFrame.instance.getPanel();
      panel.getParent();
      panel.removeAll();

      GridBagConstraints gbc = new GridBagConstraints();
      this.addLauncherPanelComponents(panel, gbc);

      panel.revalidate();
      panel.repaint();
    }
  }
}
