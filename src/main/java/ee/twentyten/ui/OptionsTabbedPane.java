package ee.twentyten.ui;

import ee.twentyten.ui.panel.VersionsOptionsPanel;
import javax.swing.JTabbedPane;
import lombok.Getter;

@Getter
public class OptionsTabbedPane extends JTabbedPane {

  private final VersionsOptionsPanel versionsOptionsPanel;

  public OptionsTabbedPane() {
    super(JTabbedPane.TOP);

    this.versionsOptionsPanel = new VersionsOptionsPanel();
    this.add("Versions", this.versionsOptionsPanel);
  }
}
