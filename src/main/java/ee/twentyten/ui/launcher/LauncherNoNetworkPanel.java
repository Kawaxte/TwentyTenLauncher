package ee.twentyten.ui.launcher;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.component.TransparentJButton;
import ee.twentyten.custom.ui.CustomJPanel;
import ee.twentyten.custom.ui.TransparentPanelUI;
import ee.twentyten.util.LanguageUtils;
import ee.twentyten.util.LauncherUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.MinecraftUtils;

public class LauncherNoNetworkPanel extends CustomJPanel implements ActionListener {

  @Getter
  @Setter
  private static LauncherNoNetworkPanel instance;
  @Getter
  private final JLabel errorLabel;
  private final JLabel playOnlineLabel;
  private final TransparentJButton playOfflineButton;
  private final TransparentJButton tryAgainButton;

  {
    this.errorLabel = new JLabel("\u00A0", JLabel.CENTER);
    this.playOnlineLabel = new JLabel("lnnp.label.playOnlineLabel");
    this.playOfflineButton = new TransparentJButton("lnnp.button.playOfflineButton");
    this.tryAgainButton = new TransparentJButton("lnnp.button.tryAgainButton");

    this.playOfflineButton.addActionListener(this);
    this.tryAgainButton.addActionListener(this);
  }

  public LauncherNoNetworkPanel() {
    super(new BorderLayout(0, 10), true);

    LauncherNoNetworkPanel.setInstance(this);
    this.buildTopPanel();
    this.buildMiddlePanel();
    this.buildBottomPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.playOnlineLabel,
        "lnnp.label.playOnlineLabel");
    LanguageUtils.setTextToComponent(bundle, this.playOfflineButton,
        "lnnp.button.playOfflineButton");
    LanguageUtils.setTextToComponent(bundle, this.tryAgainButton, "lnnp.button.tryAgainButton");
  }

  private void buildTopPanel() {
    this.errorLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
    this.errorLabel.setForeground(Color.RED.darker());

    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setUI(new TransparentPanelUI());
    topPanel.add(this.errorLabel, BorderLayout.NORTH);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.setUI(new TransparentPanelUI());
    if (!MinecraftUtils.isMinecraftCached()) {
      middlePanel.add(this.playOnlineLabel, BorderLayout.SOUTH);
    }
    this.add(middlePanel, BorderLayout.CENTER);
  }

  private void buildBottomPanel() {
    this.playOfflineButton.setEnabled(MinecraftUtils.isMinecraftCached());

    JPanel bottomPanel = new JPanel(new GridLayout(1, 2), true);
    bottomPanel.setUI(new TransparentPanelUI());
    bottomPanel.add(this.playOfflineButton, BorderLayout.WEST);
    bottomPanel.add(this.tryAgainButton, BorderLayout.EAST);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.tryAgainButton) {
      LauncherUtils.addPanel(LauncherPanel.getInstance(), new LauncherPanel());
    }
  }
}
