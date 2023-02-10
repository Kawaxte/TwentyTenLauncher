package ee.twentyten.ui.options;

import ee.twentyten.util.LanguageHelper;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import lombok.Getter;

@Getter
public class OptionsLanguagePanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final String translationsTextAreaText;
  private final String setLanguageLabelText;
  @Getter
  private JComboBox<String> setLanguageComboBox;

  {
    this.translationsTextAreaText = LanguageHelper.getString("olp.textarea.languagesTextArea.text");
    this.setLanguageLabelText = LanguageHelper.getString("olp.label.setLanguageLabel.text");
  }

  public OptionsLanguagePanel() {
    super(new BorderLayout(), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.initComponents();
  }

  private void initComponents() {
    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    this.add(middlePanel, BorderLayout.NORTH);

    JTextArea translationsTextArea = new JTextArea(this.translationsTextAreaText, 4, 1);
    translationsTextArea.setEditable(false);
    translationsTextArea.setLineWrap(true);
    translationsTextArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(translationsTextArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    middlePanel.add(scrollPane, BorderLayout.CENTER);
  }

  private void createBottomPanel() {

    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    JLabel setLanguageLabel = new JLabel(this.setLanguageLabelText, SwingConstants.RIGHT);
    bottomPanel.add(setLanguageLabel, BorderLayout.WEST);

    this.setLanguageComboBox = new JComboBox<>();
    bottomPanel.add(this.setLanguageComboBox, BorderLayout.CENTER);
  }
}
