package ee.twentyten.ui.options;

import ee.twentyten.config.LauncherConfig;
import ee.twentyten.lang.ELanguage;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.util.OptionsHelper;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
    this.translationsTextAreaText = LauncherLanguage
        .getString("olp.textarea.languagesTextArea");
    this.setLanguageLabelText = LauncherLanguage
        .getString("olp.label.setLanguageLabel");
  }

  public OptionsLanguagePanel() {
    super(new BorderLayout(0, 5), true);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    this.createMiddlePanel();
    this.createBottomPanel();
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    this.add(middlePanel, BorderLayout.NORTH);

    JTextArea translationsTextArea = new JTextArea(
        this.translationsTextAreaText, 4, 1);
    translationsTextArea.setEditable(false);
    translationsTextArea.setLineWrap(true);
    translationsTextArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(translationsTextArea);
    scrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    middlePanel.add(scrollPane, BorderLayout.CENTER);
  }

  private void createBottomPanel() {
    JPanel bottomPanel = new JPanel(new BorderLayout(), true);
    this.add(bottomPanel, BorderLayout.SOUTH);

    JLabel setLanguageLabel = new JLabel(this.setLanguageLabelText,
        SwingConstants.RIGHT);
    bottomPanel.add(setLanguageLabel, BorderLayout.WEST);

    this.setLanguageComboBox = new JComboBox<>();
    bottomPanel.add(this.setLanguageComboBox, BorderLayout.CENTER);
  }

  public void updateSetLanguageList() {
    OptionsHelper.languages = new HashMap<>();

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    for (ELanguage language : ELanguage.values()) {
      String languageName = language.getName();
      String languageValue = language.toString().substring(9);
      model.addElement(languageName);

      OptionsHelper.languages.put(languageName,
          languageValue.toLowerCase(Locale.ROOT));
    }

    String selectedLanguage = LauncherConfig.instance.getSelectedLanguage();
    for (Map.Entry<String, String> entry : OptionsHelper.languages.entrySet()) {
      if (entry.getValue().equals(selectedLanguage)) {
        model.setSelectedItem(entry.getKey());
        break;
      }
    }
    this.setLanguageComboBox.setModel(model);
  }
}
