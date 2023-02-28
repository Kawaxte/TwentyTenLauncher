package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.custom.TransparentJButton;
import ee.twentyten.custom.TransparentPanelUI;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.util.LanguageUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class LauncherNoNetworkPanel extends CustomJPanel implements ActionListener {
  
  @Getter
  @Setter
  public static LauncherNoNetworkPanel instance;
  private final JLabel playOnlineLabel;
  private final TransparentJButton playOfflineButton;
  private final TransparentJButton tryAgainButton;

  {
    this.playOnlineLabel = new JLabel(LanguageUtils.playOnlineLabelKey);
    this.playOfflineButton = new TransparentJButton(LanguageUtils.playOfflineButtonKey);
    this.tryAgainButton = new TransparentJButton(LanguageUtils.tryAgainButtonKey);

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
        LanguageUtils.playOnlineLabelKey);
    LanguageUtils.setTextToComponent(bundle, this.playOfflineButton,
        LanguageUtils.playOfflineButtonKey);
    LanguageUtils.setTextToComponent(bundle, this.tryAgainButton, LanguageUtils.tryAgainButtonKey);
  }

  private void buildTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setUI(new TransparentPanelUI());
    topPanel.add(LauncherLoginPanel.getInstance().getErrorLabel(), BorderLayout.NORTH);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.setUI(new TransparentPanelUI());
    middlePanel.add(this.playOnlineLabel, BorderLayout.SOUTH);
    /*
    if (!MinecraftLauncher.isMinecraftCached()) {
      middlePanel.add(this.playOnlineLabel, BorderLayout.SOUTH);
    }
    */
    this.add(middlePanel, BorderLayout.CENTER);
  }

  private void buildBottomPanel() {
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
      LauncherPanel.getInstance().show(new LauncherPanel());
    }
  }
}
