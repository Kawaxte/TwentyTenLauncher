package ee.twentyten.ui.options;

import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.custom.component.JGroupBox;
import ee.twentyten.util.LanguageUtils;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;

public class LanguageOptionsGroupBox extends JGroupBox {

  @Getter
  @Setter
  private static LanguageOptionsGroupBox instance;
  private final JLabel setLanguageLabel;
  @Getter
  private final JComboBox<String> setLanguageComboBox;

  {
    this.setLanguageLabel = new JLabel("logb.label.setLanguageLabel", JLabel.RIGHT);
    this.setLanguageComboBox = new JComboBox<>();
  }

  public LanguageOptionsGroupBox() {
    super("logb.string.title");

    LanguageOptionsGroupBox.setInstance(this);
    this.buildTopPanel();

    LanguageUtils.updateLanguageComboBox(this);
    this.setTextToContainers(LanguageUtils.getBundle());
    this.setTextToComponents(LanguageUtils.getBundle());
  }

  private void buildTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.add(this.setLanguageLabel, BorderLayout.WEST);
    topPanel.add(this.setLanguageComboBox, BorderLayout.CENTER);
    this.add(topPanel, BorderLayout.NORTH);
  }

  public void setTextToContainers(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToContainer(bundle, this, "logb.string.title");
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.setLanguageLabel, "logb.label.setLanguageLabel");
  }
}
