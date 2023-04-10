package io.github.kawaxte.twentyten.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;

public class LauncherFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public LauncherFrame() {
    super();

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

