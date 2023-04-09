package io.github.kawaxte.twentyten.ui.options;

import io.github.kawaxte.twentyten.misc.ui.JGroupBox;
import io.github.kawaxte.twentyten.util.LauncherOptionsUtils;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.Getter;
import lombok.val;

public class LanguageGroupBox extends JGroupBox {

  public static final long serialVersionUID = 1L;
  private final JLabel setLanguageLabel;
  @Getter
  private final JComboBox<String> languageComboBox;

  {
    this.setLanguageLabel = new JLabel("lgb.setLanguageLabel",
        SwingConstants.RIGHT);
    this.languageComboBox = new JComboBox<>();
  }

  public LanguageGroupBox() {
    super("lgb.title", true);

    this.setLayout(this.getGroupLayout());

    LauncherOptionsUtils.updateLanguageComboBox(this);
  }

  private GroupLayout getGroupLayout() {
    val groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateContainerGaps(true);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setHorizontalGroup(
        groupLayout.createSequentialGroup()
            .addComponent(this.setLanguageLabel)
            .addComponent(this.languageComboBox)
    );
    groupLayout.setVerticalGroup(
        groupLayout.createSequentialGroup()
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(this.setLanguageLabel)
                .addComponent(this.languageComboBox)
            )
    );
    return groupLayout;
  }
}
