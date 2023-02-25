package ee.twentyten.custom;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class CustomJGroupBox extends JPanel {

  private static final long serialVersionUID = 1L;

  public CustomJGroupBox(String title) {
    super(true);
    
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder(title)));
  }
}
