package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.custom.TransparentJButton;
import ee.twentyten.custom.TransparentPanelUI;
import ee.twentyten.custom.UTF8ResourceBundle;
import ee.twentyten.util.LanguageUtils;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import lombok.Getter;
import lombok.Setter;

public class LauncherMicrosoftLoginPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  @Getter
  @Setter
  public static LauncherMicrosoftLoginPanel instance;
  private final JLabel copyUserCodeLabel;
  private final JLabel userCodeLabel;
  private final JProgressBar expiresInProgressBar;
  private final TransparentJButton openBrowserButton;
  private final TransparentJButton cancelButton;

  {
    this.copyUserCodeLabel = new JLabel(LanguageUtils.copyUserCodeLabelKey, JLabel.CENTER);
    this.userCodeLabel = new JLabel("\u00A0", JLabel.CENTER);
    this.expiresInProgressBar = new JProgressBar();
    this.openBrowserButton = new TransparentJButton(LanguageUtils.openBrowserButtonKey);
    this.cancelButton = new TransparentJButton(LanguageUtils.cancelButtonKey);

    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(
            LauncherMicrosoftLoginPanel.this.userCodeLabel.getText());
        clipboard.setContents(ss, ss);
      }

      @Override
      public void mousePressed(MouseEvent event) {
        LauncherMicrosoftLoginPanel.this.openBrowserButton.setEnabled(true);
      }

      @Override
      public void mouseEntered(MouseEvent event) {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        LauncherMicrosoftLoginPanel.this.userCodeLabel.setCursor(handCursor);
      }
    };
    this.userCodeLabel.addMouseListener(adapter);
    this.openBrowserButton.addActionListener(this);
    this.cancelButton.addActionListener(this);
  }

  public LauncherMicrosoftLoginPanel() {
    super(new BorderLayout(0, 10), true);

    LauncherMicrosoftLoginPanel.setInstance(this);
    this.buildTopPanel();
    this.buildMiddlePanel();
    this.buildBottomPanel();

    this.setTextToComponents(LanguageUtils.getBundle());
  }

  public void setTextToComponents(UTF8ResourceBundle bundle) {
    LanguageUtils.setTextToComponent(bundle, this.copyUserCodeLabel,
        LanguageUtils.copyUserCodeLabelKey);
    LanguageUtils.setTextToComponent(bundle, this.openBrowserButton,
        LanguageUtils.openBrowserButtonKey);
    LanguageUtils.setTextToComponent(bundle, this.cancelButton, LanguageUtils.cancelButtonKey);
  }

  private void buildTopPanel() {
    Font userCodeFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    this.userCodeLabel.setFont(userCodeFont);

    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setUI(new TransparentPanelUI());
    topPanel.add(this.copyUserCodeLabel, BorderLayout.NORTH);
    topPanel.add(this.userCodeLabel, BorderLayout.CENTER);
    this.add(topPanel, BorderLayout.NORTH);
  }

  private void buildMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.setUI(new TransparentPanelUI());
    middlePanel.add(this.expiresInProgressBar, BorderLayout.NORTH);
    this.add(middlePanel, BorderLayout.CENTER);
  }

  private void buildBottomPanel() {
    this.openBrowserButton.setEnabled(false);

    JPanel bottomPanel = new JPanel(new GridLayout(1, 2), true);
    bottomPanel.setUI(new TransparentPanelUI());
    bottomPanel.add(this.openBrowserButton, 0);
    bottomPanel.add(this.cancelButton, 1);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == this.cancelButton) {
      LauncherPanel.getInstance().show(new LauncherPanel());
    }
  }
}
