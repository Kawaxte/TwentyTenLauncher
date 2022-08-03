/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.auth;

import net.minecraft.launcher.LFrame;
import net.minecraft.launcher.LUpdater;

import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class APanel extends Panel {
    private final APanelGraphics authPanelGraphics = new APanelGraphics(this);
    private final ALastLogin authLastLogin = new ALastLogin(this);
    private static final String updateUrl = "https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest";
    private static final String registerUrl = "https://signup.live.com/signup"
            + "?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d"
            + "&client_id=00000000402b5328"
            + "&lic=1";
    private Image image;
    private VolatileImage volatileImage;
    public Label errorLabel = new Label("", 1);
    public TextField usernameTextField = new TextField(20);
    public TextField passwordTextField = new TextField(20);
    public Checkbox rememberCheckbox = new Checkbox("Remember password");
    public Button loginButton = new Button("Login");
    public Button retryButton = new Button("Try again");
    public Button offlineButton = new Button("Play offline");

    public APanel(final LFrame launcherFrame) {
        this.setLayout(new GridBagLayout());
        this.add(this.buildLoginPanel());
        this.getUsername();
        this.retryButton.addActionListener(ae -> {
            this.errorLabel.setText("");
            this.removeAll();
            this.add(this.buildLoginPanel());
            this.validate();
        });
        this.offlineButton.addActionListener(e -> launcherFrame.playOffline(this.usernameTextField.getText()));
        this.loginButton.addActionListener(ae -> {
            try {
                launcherFrame.getYggdrasilAuthenticate(this.usernameTextField.getText(), this.passwordTextField.getText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("dirt.png"))).getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Graphics g) {
        authPanelGraphics.update(g);
    }

    public void paint(Graphics g2) {

        authPanelGraphics.paint(g2);
    }

    private Panel buildLoginPanel() {
        Panel panel = new Panel() {
            public Insets getInsets() {
                return new Insets(12, 24, 16, 32);
            }

            public void update(Graphics g) {
                this.paint(g);
            }

            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
                g.drawRect(1, 1, this.getWidth() - 3, this.getHeight() - 3);
                g.setColor(Color.WHITE);
                g.drawRect(2, 2, this.getWidth() - 5, this.getHeight() - 5);
            }
        };
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBackground(Color.GRAY);
        panel.add(this.errorLabel, "North");
        this.errorLabel.setFont(new Font(null, Font.ITALIC, 16));
        this.errorLabel.setForeground(new Color(128, 0, 0));

        Panel text = new Panel(new GridLayout(0, 1, 0, 2));
        panel.add(text, "West");
        text.add(new Label("Username:", 2));
        text.add(new Label("Password:", 2));
        text.add(new Label(""));

        Panel textField = new Panel(new GridLayout(0, 1, 0, 2));
        panel.add(textField, "Center");
        textField.add(this.usernameTextField);
        textField.add(this.passwordTextField);
        textField.add(this.rememberCheckbox);
        this.passwordTextField.setEchoChar('*');

        Panel onlinePanel = new Panel(new BorderLayout());
        try {
            Label accountLabel;
            if (!LUpdater.latestVersion.matches(LUpdater.currentVersion)) {
                accountLabel = new Label("", 1) {
                    public void update(Graphics g) {
                        this.paint(g);
                    }

                    public void paint(Graphics g) {
                        super.paint(g);
                        g.setColor(Color.BLUE);
                        g.drawLine(
                                this.getBounds().width / 2 - g.getFontMetrics().stringWidth(this.getText()) / 2,
                                this.getBounds().height / 2 + g.getFontMetrics().getHeight() / 2 - 1,
                                this.getBounds().width / 2 - g.getFontMetrics().stringWidth(this.getText()) / 2 + g.getFontMetrics().stringWidth(this.getText()),
                                this.getBounds().height / 2 + g.getFontMetrics().getHeight() / 2 - 1);
                    }
                };
                accountLabel.setText("You need to update the launcher!");
                accountLabel.setForeground(Color.BLUE);
                accountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                accountLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        try {
                            Desktop.getDesktop().browse(new URI(updateUrl));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
                this.loginButton.setEnabled(LUpdater.latestVersion.compareTo(LUpdater.currentVersion) < 0);
            } else {
                accountLabel = new Label("", 1) {
                    public void update(Graphics g) {
                        this.paint(g);
                    }

                    public void paint(Graphics g) {
                        super.paint(g);
                        g.setColor(Color.BLUE);
                        g.drawLine(
                                this.getBounds().width / 2 - g.getFontMetrics().stringWidth(this.getText()) / 2,
                                this.getBounds().height / 2 + g.getFontMetrics().getHeight() / 2 - 1,
                                this.getBounds().width / 2 - g.getFontMetrics().stringWidth(this.getText()) / 2 + g.getFontMetrics().stringWidth(this.getText()),
                                this.getBounds().height / 2 + g.getFontMetrics().getHeight() / 2 - 1);
                    }
                };
                accountLabel.setText("Need account?");
                accountLabel.setForeground(Color.BLUE);
                accountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                accountLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        try {
                            Desktop.getDesktop().browse(new URI(registerUrl));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            onlinePanel.add(accountLabel, "West");
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
        panel.add(onlinePanel, "South");
        onlinePanel.add(this.loginButton, "East");
        return panel;
    }

    private Panel buildOfflinePanel() {
        Panel panel = new Panel() {
            public Insets getInsets() {
                return new Insets(12, 24, 16, 32);
            }

            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
                g.drawRect(1, 1, this.getWidth() - 3, this.getHeight() - 3);
                g.setColor(Color.WHITE);
                g.drawRect(2, 2, this.getWidth() - 5, this.getHeight() - 5);
            }

            public void update(Graphics g) {
                this.paint(g);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.GRAY);
        panel.add(new Panel(), "Center");
        panel.add(this.errorLabel, "North");
        this.errorLabel.setFont(new Font(null, Font.ITALIC, 16));
        this.errorLabel.setForeground(new Color(128, 0, 0));

        Panel offlinePanel = new Panel(new BorderLayout());
        offlinePanel.add(new Panel(), "Center");
        offlinePanel.add(this.retryButton, "East");
        offlinePanel.add(this.offlineButton, "West");

        boolean canPlayOffline = LFrame.canPlayOffline(this.usernameTextField.getText());
        this.offlineButton.setEnabled(canPlayOffline);
        if (!canPlayOffline) {
            panel.add(new Label("Play online once to enable offline", 0));
        }
        panel.add(offlinePanel, "South");
        return panel;
    }

    private void getUsername() {
        authLastLogin.readUsername();
    }

    public void getLogin() {
        authLastLogin.writeUsername();
    }

    public Image getImage() {
        return this.image;
    }

    public VolatileImage getVolatileImage() {
        return this.volatileImage;
    }

    public TextField getUsernameTextField() {
        return this.usernameTextField;
    }

    public TextField getPasswordTextField() {
        return this.passwordTextField;
    }

    public Checkbox getRememberCheckbox() {
        return this.rememberCheckbox;
    }

    public void setVolatileImage(VolatileImage volatileImage) {
        this.volatileImage = volatileImage;
    }

    public void setError(String error) {
        this.removeAll();
        this.add(this.buildLoginPanel());
        this.errorLabel.setText(error);
        this.validate();
    }

    public void setNoNetwork() {
        this.removeAll();
        this.add(this.buildOfflinePanel());
        this.validate();
    }
}