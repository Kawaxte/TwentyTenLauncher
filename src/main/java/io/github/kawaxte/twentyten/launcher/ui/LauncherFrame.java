package io.github.kawaxte.twentyten.launcher.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Optional;
import javax.swing.JFrame;
import lombok.val;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public LauncherFrame() {
    super();

    val iconUrl =
        Optional.ofNullable(LauncherFrame.class.getClassLoader().getResource("favicon.png"))
            .orElseThrow(() -> new NullPointerException("iconUrl must not be null"));
    this.setIconImage(this.getToolkit().getImage(iconUrl));

    this.setLayout(new CardLayout(0, 0));
    this.setContentPane(new LauncherPanel());

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(640, 480));
    this.setPreferredSize(new Dimension(854, 480));

    this.pack();

    this.setLocationRelativeTo(null);
    this.setResizable(true);
  }

  @Override
  public String getTitle() {
    return "TwentyTen Launcher";
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    this.getContentPane().setFont(font);
  }
}
