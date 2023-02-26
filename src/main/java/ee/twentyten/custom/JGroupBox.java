package ee.twentyten.custom;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class JGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;

  public JGroupBox(String title) {
    super(true);

    this.setTitle(title);
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  public void setTitle(String title) {
    this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder(title)));
  }
}
