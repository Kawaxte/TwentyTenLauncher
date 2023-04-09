package io.github.kawaxte.twentyten.misc.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class JGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;
  private TitledBorder border;

  public JGroupBox(String title, boolean isDoubleBuffered) {
    super(isDoubleBuffered);

    this.setTitledBorder(title);
  }

  public String getTitle() {
    return this.border.getTitle();
  }

  public void setTitle(String title) {
    this.border.setTitle(title);
  }

  public void setTitledBorder(String title) {
    this.border = BorderFactory.createTitledBorder(title);
    super.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder(title))
    );
  }
}
