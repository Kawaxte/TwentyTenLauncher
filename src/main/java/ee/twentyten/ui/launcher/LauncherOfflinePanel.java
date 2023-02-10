package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.util.LanguageHelper;
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

public class LauncherOfflinePanel extends CustomJPanel {

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
    this.playOnlineLabelText = LanguageHelper.getString("lop.label.playOnlineLabel.text");
    this.playOfflineButtonText = LanguageHelper.getString("lop.button.playOfflineButton.text");
    this.tryAgainButtonText = LanguageHelper.getString("lop.button.tryAgainButton.text");
  }

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

    this.playOnlineLabel = new JLabel(this.playOnlineLabelText, SwingConstants.LEFT);

    this.createBottomPanel();
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 0, 0), true);
    bottomPanel.setBackground(Color.GRAY);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.playOfflineButton = new JButton(this.playOfflineButtonText);
    bottomPanel.add(this.playOfflineButton, BorderLayout.WEST);
    bottomPanel.add(Box.createHorizontalBox());

    this.tryAgainButton = new JButton(this.tryAgainButtonText);
    bottomPanel.add(this.tryAgainButton, BorderLayout.EAST);
  }
}
