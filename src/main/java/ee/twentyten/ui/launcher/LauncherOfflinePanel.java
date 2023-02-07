package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.Getter;
import net.minecraft.MinecraftLauncher;

public class LauncherOfflinePanel extends CustomJPanel {

  private static final long serialVersionUID = 1L;
  @Getter
  private JLabel errorLabel;
  @Getter
  private JLabel playOnlineLabel;
  @Getter
  private JButton playOfflineButton;
  @Getter
  private JButton tryAgainButton;

  public LauncherOfflinePanel() {
    super(new BorderLayout(0, 8), true);

    this.setBackground(Color.GRAY);

    this.initComponents();
  }

  private void initComponents() {
    this.errorLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.errorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
    this.errorLabel.setForeground(Color.RED.darker());
    this.add(this.errorLabel, BorderLayout.NORTH);

    if (!MinecraftLauncher.isMinecraftCached()) {
      this.playOnlineLabel = new JLabel("Play online once to enable offline", SwingConstants.LEFT);
      this.add(this.playOnlineLabel, BorderLayout.CENTER);
    }

    this.createBottomPanel();
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 0, 0), true);
    bottomPanel.setBackground(Color.GRAY);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.playOfflineButton = new JButton("Play offline");
    bottomPanel.add(this.playOfflineButton, BorderLayout.WEST);

    bottomPanel.add(Box.createHorizontalBox());

    this.tryAgainButton = new JButton("Try again");
    bottomPanel.add(this.tryAgainButton, BorderLayout.EAST);
  }
}
