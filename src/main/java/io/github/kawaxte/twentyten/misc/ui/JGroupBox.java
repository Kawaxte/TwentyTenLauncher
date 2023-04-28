package io.github.kawaxte.twentyten.misc.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class JGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;

  public JGroupBox(String title, boolean isDoubleBuffered) {
    super(isDoubleBuffered);

    this.setTitledBorder(title);
  }

  public String setTitledBorder(String title) {
    super.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(), BorderFactory.createTitledBorder(title)));
    return title;
  }
}
