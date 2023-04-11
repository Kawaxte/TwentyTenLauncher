package io.github.kawaxte.twentyten.ui;

import io.github.kawaxte.twentyten.misc.ui.CustomJPanel;
import io.github.kawaxte.twentyten.misc.ui.TransparentJButton;
import io.github.kawaxte.twentyten.util.LauncherUtils;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import lombok.val;
import org.apache.logging.log4j.LogManager;

public class MicrosoftLoginPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private final JLabel statusLabel;
  private final JLabel userCodeLabel;
  private final JProgressBar expiresInProgressBar;
  private final TransparentJButton openBrowserButton;
  private final TransparentJButton cancelButton;
  private URL verificationUri;

  static {
    LauncherUtils.logger = LogManager.getLogger(MicrosoftLoginPanel.class);
  }

  {
    this.statusLabel = new JLabel("mlp.statusLabel",
        SwingConstants.CENTER);
    this.userCodeLabel = new JLabel("",
        SwingConstants.CENTER);
    this.expiresInProgressBar = new JProgressBar();
    this.openBrowserButton = new TransparentJButton("mlp.openBrowserButton");
    this.cancelButton = new TransparentJButton("mlp.cancelButton");
  }

  public MicrosoftLoginPanel() {
    super(true);

    this.userCodeLabel.setFont(this.userCodeLabel.getFont().deriveFont(24f));

    this.openBrowserButton.addActionListener(this);
    this.cancelButton.addActionListener(this);

    this.setLayout(this.getGroupLayout());

    this.updateComponentKeyValues();
  }

  public MicrosoftLoginPanel(String userCode, int expiresIn, URL verificationUri) {
    this();

    this.userCodeLabel.setText(userCode);
    this.expiresInProgressBar.setMaximum(expiresIn);
    this.expiresInProgressBar.setValue(expiresIn);
    this.verificationUri = verificationUri;
  }

  private void updateComponentKeyValues() {
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.statusLabel,
        this.statusLabel.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.openBrowserButton,
        this.openBrowserButton.getText());
    LauncherUtils.updateComponentKeyValue(LauncherUtils.getUtf8Bundle(),
        this.cancelButton,
        this.cancelButton.getText());
  }

  private LayoutManager getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createSequentialGroup()
            .addGroup(groupLayout.createParallelGroup()
                .addComponent(this.statusLabel,
                    0,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(this.userCodeLabel,
                    0,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(this.expiresInProgressBar)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(this.openBrowserButton,
                        0,
                        0,
                        Short.MAX_VALUE)
                    .addComponent(this.cancelButton,
                        0,
                        0,
                        Short.MAX_VALUE))));
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.statusLabel,
                0,
                GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addComponent(this.userCodeLabel,
                0,
                GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addComponent(this.expiresInProgressBar)
            .addGroup(groupLayout.createParallelGroup()
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
      LauncherUtils.addPanel(this.getParent(),
          new LauncherPanel());
    }
  }
}
