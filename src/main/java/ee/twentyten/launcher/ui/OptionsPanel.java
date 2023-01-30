package ee.twentyten.launcher.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import lombok.Getter;

@Getter
public class OptionsPanel extends JPanel {

  private JTabbedPane tabbedPane;
  private OptionsVersionsPanel optionsVersionsPanel;
  private JButton cancelButton;
  private JButton openGameDirectoryButton;
  private JButton saveOptionsButton;

  public OptionsPanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

    this.initComponents();
  }

  private void initComponents() {
    this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    this.optionsVersionsPanel = new OptionsVersionsPanel();
    this.tabbedPane.add("Versions", this.optionsVersionsPanel);
    this.add(this.tabbedPane);

    this.cancelButton = new JButton("Cancel");
    this.openGameDirectoryButton = new JButton("Open Game Directory");
    this.saveOptionsButton = new JButton("Save Options");
    this.createBottomPanel();
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
    bottomPanel.add(this.cancelButton);
    bottomPanel.add(
        Box.createHorizontalStrut(this.tabbedPane.getPreferredSize().width >> 2));
    bottomPanel.add(this.openGameDirectoryButton);
    bottomPanel.add(this.saveOptionsButton);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
