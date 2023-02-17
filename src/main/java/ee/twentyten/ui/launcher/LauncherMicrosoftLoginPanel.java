package ee.twentyten.ui.launcher;

import ee.twentyten.custom.CustomJPanel;
import ee.twentyten.lang.LauncherLanguage;
import ee.twentyten.ui.LauncherFrame;
import ee.twentyten.util.RuntimeHelper;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import lombok.Getter;
import lombok.Setter;

public class LauncherMicrosoftLoginPanel extends CustomJPanel implements
    ActionListener {

  private static final long serialVersionUID = 1L;
  private final LauncherPanel panel;
  private final String copyUserCodeLabelText;
  private final String openBrowserButtonText;
  private final String cancelButtonText;
  @Setter
  private String verificationUri;
  @Getter
  private JLabel userCodeLabel;
  private JProgressBar expiresInProgressBar;
  private JButton openBrowserButton;
  private JButton cancelButton;
  @Setter
  private int expiresIn;

  {
    this.panel = LauncherFrame.instance.getPanel();
    this.panel.getParent();

    this.copyUserCodeLabelText = LauncherLanguage.getString(
        "lmlp.label.copyUserCodeLabel");
    this.openBrowserButtonText = LauncherLanguage.getString(
        "lmlp.button.openBrowserButton");
    this.cancelButtonText = LauncherLanguage.getString(
        "lmlp.button.cancelButton");
  }

  public LauncherMicrosoftLoginPanel() {
    super(new BorderLayout(0, 8), true);

    this.createTopPanel();
    this.createMiddlePanel();
    this.createBottomPanel();

    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent evt) {
        StringSelection selection = new StringSelection(
            LauncherMicrosoftLoginPanel.this.userCodeLabel.getText());

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
      }
    };
    this.userCodeLabel.addMouseListener(adapter);
    this.openBrowserButton.addActionListener(this);
    this.cancelButton.addActionListener(this);
  }

  private void createTopPanel() {
    JPanel topPanel = new JPanel(new BorderLayout(), true);
    topPanel.setBackground(this.getBackground());
    this.add(topPanel, BorderLayout.NORTH);

    JLabel copyUserCodeLabel = new JLabel(this.copyUserCodeLabelText);
    topPanel.add(copyUserCodeLabel, BorderLayout.NORTH);
  }

  private void createMiddlePanel() {
    JPanel middlePanel = new JPanel(new BorderLayout(), true);
    middlePanel.setBackground(this.getBackground());
    this.add(middlePanel, BorderLayout.CENTER);

    Font userCodeFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    this.userCodeLabel = new JLabel("\u00A0", SwingConstants.CENTER);
    this.userCodeLabel.setFont(userCodeFont);
    middlePanel.add(this.userCodeLabel, BorderLayout.CENTER);
  }

  private void createBottomPanel() {
    JPanel northernBottomPanel = new JPanel(new BorderLayout(0, 8), true);
    northernBottomPanel.setBackground(this.getBackground());
    this.add(northernBottomPanel, BorderLayout.SOUTH);

    this.expiresInProgressBar = new JProgressBar();
    northernBottomPanel.add(this.expiresInProgressBar, BorderLayout.NORTH);

    JPanel southernBottomPanel = new JPanel(new GridLayout(1, 2), true);
    southernBottomPanel.setBackground(this.getBackground());
    northernBottomPanel.add(southernBottomPanel, BorderLayout.SOUTH);

    this.openBrowserButton = new JButton(this.openBrowserButtonText);
    southernBottomPanel.add(this.openBrowserButton, 0);

    this.cancelButton = new JButton(this.cancelButtonText);
    southernBottomPanel.add(this.cancelButton, 1);
  }

  public void startProgressBar() {
    this.expiresInProgressBar.setMinimum(0);
    this.expiresInProgressBar.setMaximum(this.expiresIn);
    this.expiresInProgressBar.setValue(this.expiresIn);

    Timer expireInTimer = new Timer(1000, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();

        int currentValue = LauncherMicrosoftLoginPanel.this.expiresInProgressBar.getValue();
        if (currentValue > 1) {
          currentValue--;

          LauncherMicrosoftLoginPanel.this.expiresInProgressBar.setValue(
              currentValue);
        } else {
          ((Timer) src).stop();
        }
      }
    });
    expireInTimer.start();
  }

  private void addLauncherPanel() {
    this.panel.removeAll();

    this.panel.add(new LauncherPanel());

    this.panel.revalidate();
    this.panel.repaint();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == this.openBrowserButton) {
      RuntimeHelper.openBrowser(this.verificationUri);
    }
    if (src == this.cancelButton) {
      this.addLauncherPanel();
    }
  }
}
