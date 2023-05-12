package io.github.kawaxte.twentyten.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Optional;
import javax.swing.JFrame;
import lombok.val;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  public static LauncherFrame instance;

  public LauncherFrame() {
    super();

    LauncherFrame.instance = this;
    val iconUrl =
        Optional.ofNullable(LauncherFrame.class.getClassLoader().getResource("favicon.png"))
            .orElseThrow(() -> new NullPointerException("iconUrl cannot be null"));
    this.setIconImage(this.getToolkit().getImage(iconUrl));

    this.setLayout(new BorderLayout());
    this.setContentPane(new LauncherPanel());

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setMinimumSize(new Dimension(640 + 16, 480 + 39));
    this.setPreferredSize(new Dimension(854 + 16, 480 + 39));

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
