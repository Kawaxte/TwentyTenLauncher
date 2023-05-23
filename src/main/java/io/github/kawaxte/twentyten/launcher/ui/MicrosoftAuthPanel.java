/*
 * Copyright (C) 2023 Kawaxte
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.kawaxte.twentyten.launcher.ui;

import io.github.kawaxte.twentyten.UTF8ResourceBundle;
import io.github.kawaxte.twentyten.launcher.LauncherConfig;
import io.github.kawaxte.twentyten.launcher.LauncherLanguage;
import io.github.kawaxte.twentyten.launcher.ui.custom.CustomJPanel;
import io.github.kawaxte.twentyten.launcher.ui.custom.TransparentJButton;
import io.github.kawaxte.twentyten.launcher.util.LauncherLanguageUtils;
import io.github.kawaxte.twentyten.launcher.util.LauncherUtils;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import lombok.Getter;

@Getter
public class MicrosoftAuthPanel extends CustomJPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  private static MicrosoftAuthPanel instance;
  private final JLabel copyCodeLabel;
  private final JLabel userCodeLabel;
  private final JProgressBar expiresInProgressBar;
  private final TransparentJButton openBrowserButton;
  private final TransparentJButton cancelButton;
  private String verificationUri;

  public MicrosoftAuthPanel() {
    super(true);

    setInstance(this);

    this.copyCodeLabel = new JLabel(LauncherLanguageUtils.getMAPKeys()[0], SwingConstants.CENTER);
    this.userCodeLabel = new JLabel("", SwingConstants.CENTER);
    this.expiresInProgressBar = new JProgressBar();
    this.openBrowserButton = new TransparentJButton(LauncherLanguageUtils.getMAPKeys()[1]);
    this.cancelButton = new TransparentJButton(LauncherLanguageUtils.getMAPKeys()[2]);

    this.setLayout(this.getGroupLayout());

    this.userCodeLabel.setFont(this.userCodeLabel.getFont().deriveFont(Font.BOLD, 24f));
    this.userCodeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    this.userCodeLabel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent event) {
            Clipboard clipboard = getToolkit().getSystemClipboard();
            StringSelection transferable = new StringSelection(userCodeLabel.getText());
            clipboard.setContents(transferable, null);
          }
        });
    this.openBrowserButton.addActionListener(this);
    this.cancelButton.addActionListener(this);

    String selectedLanguage = (String) LauncherConfig.get(0);
    UTF8ResourceBundle bundle = LauncherLanguage.getUTF8Bundle(selectedLanguage);
    this.updateComponentKeyValues(
        Objects.nonNull(selectedLanguage) ? bundle : LauncherLanguage.getBundle());
  }

  public MicrosoftAuthPanel(String userCode, String verificationUri, String expiresIn) {
    this();
    this.userCodeLabel.setText(userCode);
    this.expiresInProgressBar.setMaximum(Integer.parseInt(expiresIn));
    this.expiresInProgressBar.setValue(Integer.parseInt(expiresIn));
    this.verificationUri = verificationUri;
  }

  public static MicrosoftAuthPanel getInstance() {
    return instance;
  }

  private static void setInstance(MicrosoftAuthPanel map) {
    MicrosoftAuthPanel.instance = map;
  }

  public void updateComponentKeyValues(UTF8ResourceBundle bundle) {
    LauncherUtils.updateComponentKeyValue(
        bundle, this.copyCodeLabel, LauncherLanguageUtils.getMAPKeys()[0]);
    LauncherUtils.updateComponentKeyValue(
        bundle, this.openBrowserButton, LauncherLanguageUtils.getMAPKeys()[1]);
    LauncherUtils.updateComponentKeyValue(
        bundle, this.cancelButton, LauncherLanguageUtils.getMAPKeys()[2]);
  }

  private LayoutManager getGroupLayout() {
    int width = 0;

    JButton[] buttons = new JButton[] {this.openBrowserButton, this.cancelButton};
    for (JButton button : buttons) {
      width = Math.max(width, button.getPreferredSize().width);
    }

    GroupLayout gl = new GroupLayout(this);
    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(
        gl.createSequentialGroup()
            .addGroup(
                gl.createParallelGroup()
                    .addComponent(this.copyCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.userCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(this.expiresInProgressBar)
                    .addGroup(
                        gl.createSequentialGroup()
                            .addComponent(this.openBrowserButton, 0, width, Short.MAX_VALUE)
                            .addComponent(this.cancelButton, 0, width, Short.MAX_VALUE))));
    gl.setVerticalGroup(
        gl.createSequentialGroup()
            .addComponent(this.copyCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(this.userCodeLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(this.expiresInProgressBar)
            .addGroup(
                gl.createParallelGroup()
                    .addComponent(this.openBrowserButton)
                    .addComponent(this.cancelButton)));
    return gl;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (Objects.equals(source, this.openBrowserButton)) {
      Clipboard clipboard = this.getToolkit().getSystemClipboard();
      StringSelection transferable = new StringSelection(this.userCodeLabel.getText());
      clipboard.setContents(transferable, null);

      LauncherUtils.openBrowser(this.verificationUri);
    }
    if (Objects.equals(source, this.cancelButton)) {
      LauncherUtils.swapContainers(this.getParent(), new LauncherPanel());
    }
  }
}
