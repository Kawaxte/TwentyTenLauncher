package io.github.kawaxte.twentyten.launcher.ui.auth;

import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.configInstance;
import static io.github.kawaxte.twentyten.launcher.util.LauncherConfigUtils.languageInstance;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.ui.LauncherPanel;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import io.github.kawaxte.twentyten.ui.CustomJPanel;
import io.github.kawaxte.twentyten.ui.TransparentJButton;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

@Getter
public class MicrosoftAuthPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  public static MicrosoftAuthPanel instance;
  private final JLabel copyCodeLabel;
  private final JLabel userCodeLabel;
  private final JProgressBar expiresInProgressBar;
  private final TransparentJButton openBrowserButton;
  private final TransparentJButton cancelButton;
  private String verificationUri;

  {
    this.copyCodeLabel = new JLabel("map.copyCodeLabel", SwingConstants.CENTER);
    this.userCodeLabel = new JLabel("", SwingConstants.CENTER);
    this.expiresInProgressBar = new JProgressBar();
    this.openBrowserButton = new TransparentJButton("map.openBrowserButton");
    this.cancelButton = new TransparentJButton("map.cancelButton");
  }

  public MicrosoftAuthPanel() {
    super(true);

    MicrosoftAuthPanel.instance = this;
    this.userCodeLabel.setFont(this.userCodeLabel.getFont().deriveFont(Font.BOLD, 24f));
    this.userCodeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    this.openBrowserButton.setEnabled(false);

    this.userCodeLabel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent event) {
            val clipboard = LauncherPanel.instance.getToolkit().getSystemClipboard();
            val transferable = new StringSelection(userCodeLabel.getText());
            clipboard.setContents(transferable, null);

            openBrowserButton.setEnabled(true);
          }
        });
    this.openBrowserButton.addActionListener(this);
    this.cancelButton.addActionListener(this);

    this.setLayout(this.getGroupLayout());

    val selectedLanguage = configInstance.getSelectedLanguage();
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage)
            ? LauncherLanguageUtils.getUTF8Bundle(selectedLanguage)
            : languageInstance.getBundle());
  }

  public MicrosoftAuthPanel(String userCode, String verificationUri, String expiresIn) {
    this();
    this.userCodeLabel.setText(userCode);
    this.expiresInProgressBar.setMaximum(Integer.parseInt(expiresIn));
    this.expiresInProgressBar.setValue(Integer.parseInt(expiresIn));
    this.verificationUri = verificationUri;
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(bundle, this.copyCodeLabel, "map.copyCodeLabel");
    LauncherUtils.updateComponentKeyValue(bundle, this.openBrowserButton, "map.openBrowserButton");
    LauncherUtils.updateComponentKeyValue(bundle, this.cancelButton, "map.cancelButton");
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout
            .createSequentialGroup()
            .addGroup(
                groupLayout
                    .createParallelGroup()
                    .addComponent(this.copyCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.userCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.expiresInProgressBar)
                    .addGroup(
                        groupLayout
                            .createSequentialGroup()
                            .addComponent(this.openBrowserButton, 0, 0, Short.MAX_VALUE)
                            .addComponent(this.cancelButton, 0, 0, Short.MAX_VALUE))));
    groupLayout.setVerticalGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(this.copyCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(this.userCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(this.expiresInProgressBar)
            .addGroup(
                groupLayout
                    .createParallelGroup()
                    .addComponent(this.openBrowserButton)
                    .addComponent(this.cancelButton)));
    return groupLayout;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    val source = event.getSource();
    if (Objects.equals(source, this.openBrowserButton)) {
      LauncherUtils.openBrowser(this.verificationUri);
    }
    if (Objects.equals(source, this.cancelButton)) {
      LauncherUtils.addComponentToContainer(this.getParent(), new LauncherPanel());
    }
  }
}
