package ee.twentyten.ui.panel;

import ee.twentyten.ui.OptionsTabbedPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.Getter;

@Getter
public class OptionsPanel extends JPanel {

  private final OptionsTabbedPane optionsTabbedPane;
  private JButton closeButton;
  private JButton openGameDirectoryButton;
  private JButton saveOptionsButton;

  public OptionsPanel() {
    super(new BorderLayout(), true);

    this.optionsTabbedPane = new OptionsTabbedPane();
    this.add(this.optionsTabbedPane);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));

    this.initComponents();
  }

  private void initComponents() {
    this.createBottomPanel();
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
    this.closeButton = new JButton("Close");
    this.openGameDirectoryButton = new JButton("Open Game Directory");
    this.saveOptionsButton = new JButton("Save Options");
    bottomPanel.add(this.closeButton);
    bottomPanel.add(
        Box.createHorizontalStrut(this.optionsTabbedPane.getPreferredSize().width >> 2));
    bottomPanel.add(this.openGameDirectoryButton);
    bottomPanel.add(this.saveOptionsButton);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }
}
