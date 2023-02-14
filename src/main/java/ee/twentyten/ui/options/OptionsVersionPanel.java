package ee.twentyten.ui.options;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.LauncherLanguage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import lombok.Getter;

@Getter
public class OptionsVersionPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final String showVersionsCheckboxText;
  private final String useVersionLabelText;
  private JCheckBox showBetaVersionsCheckBox;
  private JCheckBox showAlphaVersionsCheckBox;
  private JCheckBox showInfdevVersionsCheckBox;
  private JLabel useVersionLabel;
  private JComboBox<String> useVersionComboBox;

  {
    this.showVersionsCheckboxText = LauncherLanguage
        .getString("ovp.checkbox.showVersionsCheckBox");
    this.useVersionLabelText = LauncherLanguage
        .getString("ovp.label.useVersionLabel");
  }

  public OptionsVersionPanel(
      LayoutManager layout, boolean isDoubleBuffered
  ) {
    super(layout, isDoubleBuffered);

    Border border = BorderFactory.createEmptyBorder(
        8, 8, 8, 8);
    this.setBorder(border);

    GridLayout gridLayout = new GridLayout(
        0, 1);
    this.createMiddlePanel(gridLayout);

    BorderLayout borderLayout = new BorderLayout();
    this.createBottomPanel(borderLayout);
  }

  private void createMiddlePanel(
      LayoutManager layout
  ) {
    JPanel middlePanel = new JPanel(layout, true);
    this.add(middlePanel, SwingConstants.CENTER);

    String showBetaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText,
        "Beta", "2010-12-20 -> 2011-01-21"
    );
    String showAlphaVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText,
        "Alpha", "2010-07-02 -> 2010-12-03"
    );
    String showInfdevVersionsCheckboxText = String.format(
        this.showVersionsCheckboxText,
        "Infdev", "2010-06-29 -> 2010-06-30"
    );

    this.showBetaVersionsCheckBox = new JCheckBox(
        showBetaVersionsCheckboxText,
        LauncherConfig.instance.getUsingBeta()
    );
    this.showAlphaVersionsCheckBox = new JCheckBox(
        showAlphaVersionsCheckboxText,
        LauncherConfig.instance.getUsingAlpha()
    );
    this.showInfdevVersionsCheckBox = new JCheckBox(
        showInfdevVersionsCheckboxText,
        LauncherConfig.instance.getUsingInfdev()
    );

    middlePanel.add(this.showAlphaVersionsCheckBox, 1);
    middlePanel.add(this.showInfdevVersionsCheckBox, 2);
    middlePanel.add(this.showBetaVersionsCheckBox, 0);
  }

  private void createBottomPanel(
      LayoutManager layout
  ) {
    JPanel bottomPanel = new JPanel(layout, true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    this.useVersionLabel = new JLabel(
        this.useVersionLabelText,
        SwingConstants.RIGHT
    );
    this.useVersionComboBox = new JComboBox<>();

    bottomPanel.add(this.useVersionLabel, BorderLayout.WEST);
    bottomPanel.add(this.useVersionComboBox, BorderLayout.CENTER);
  }
}
