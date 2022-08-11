/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.launcher.auth;

import net.minecraft.launcher.LauncherFrame;
import net.minecraft.launcher.LauncherUpdate;

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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class AuthPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private final AuthPanelGraphics authPanelGraphics = new AuthPanelGraphics(this);
    private final LauncherFrame launcherFrame;
    protected Label errorLabel = new Label("", 1);
    protected static TextField usernameTextField = new TextField(20);
    protected static TextField passwordTextField = new TextField(20);
    protected static Checkbox rememberCheckbox = new Checkbox("Remember password");
    protected Button loginButton = new Button("Login");
    protected Button retryButton = new Button("Try again");
    protected Button offlineButton = new Button("Play offline");
    private Image image;
    private VolatileImage volatileImage;

    public AuthPanel(final LauncherFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
        this.setLayout(new GridBagLayout());
        this.add(this.buildLoginPanel());
        this.loginButton.addActionListener(this::loginButtonPressed);
        this.offlineButton.addActionListener(this::offlineButtonPressed);
        this.retryButton.addActionListener(this::retryButtonPressed);
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("net/minecraft/resources/dirt.png"))).getScaledInstance(32, 32, Image.SCALE_FAST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (AuthLastLogin.readLastLogin() != null) {
            if (!Objects.requireNonNull(AuthLastLogin.readLastLogin()).isValidForMicrosoft()) {
                AuthLastLogin.deleteLastLogin();
            }
        }
    }
    public void update(Graphics g) {
        authPanelGraphics.update(g);
    }

    public void paint(Graphics g2) {

        authPanelGraphics.paint(g2);
    }

    private void loginButtonPressed(ActionEvent ae) {
        if (!(LauncherUpdate.latestVersion != null
                && LauncherUpdate.latestVersion.matches(LauncherUpdate.currentVersion))) {
            this.launcherFrame.showError("Outdated launcher");
            this.launcherFrame.getAuthPanel().setNoNetwork();
            return;
        }
        if (usernameTextField.getText().equalsIgnoreCase("$MS")
                && passwordTextField.getText().equalsIgnoreCase("$MICROSOFT")) {
            launcherFrame.getMicrosoftAuthenticate().authenticate();
        } else {
            launcherFrame.showError("Login failed");
            launcherFrame.getAuthPanel().setNoNetwork();
        }
    }

    private void offlineButtonPressed(ActionEvent ae) {
        launcherFrame.getOfflineInstance(usernameTextField.getText());
    }

    private void retryButtonPressed(ActionEvent ae) {
        this.errorLabel.setText("");
        this.removeAll();
        this.add(this.buildLoginPanel());
        this.validate();
    }

    private Panel buildLoginPanel() {
        Panel panel = new Panel() {
            private static final long serialVersionUID = 1L;

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
        textField.add(usernameTextField);
        textField.add(passwordTextField);
        textField.add(rememberCheckbox);
        passwordTextField.setEchoChar('*');

        Panel onlinePanel = new Panel(new BorderLayout());
        try {
            Label accountLabel = new Label("", 1) {
                private static final long serialVersionUID = 0L;

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
            if (!(LauncherUpdate.latestVersion != null && LauncherUpdate.latestVersion.matches(LauncherUpdate.currentVersion))) {
                accountLabel.setText("You need to update the launcher!");
                accountLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/sojlabjoi/AlphacraftLauncher/releases/latest"));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
                this.loginButton.setEnabled((LauncherUpdate.latestVersion != null ? LauncherUpdate.latestVersion.compareTo(LauncherUpdate.currentVersion) : 0) < 0);
            } else {
                accountLabel.setText("Need account?");
                accountLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://signup.live.com/signup"
                                    + "?cobrandid=8058f65d-ce06-4c30-9559-473c9275a65d"
                                    + "&client_id=00000000402b5328"
                                    + "&lic=1"));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            accountLabel.setForeground(Color.BLUE);
            accountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            private static final long serialVersionUID = 1L;

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

        boolean canPlayOffline = LauncherFrame.canPlayOffline(usernameTextField.getText());
        this.offlineButton.setEnabled(canPlayOffline);
        if (!canPlayOffline) {
            panel.add(new Label("Play online once to enable offline", 0));
        }
        panel.add(offlinePanel, "South");
        return panel;
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public static TextField getUsernameTextField() {
        return usernameTextField;
    }

    public static TextField getPasswordTextField() {
        return passwordTextField;
    }

    public static Checkbox getRememberCheckbox() {
        return rememberCheckbox;
    }

    public Image getImage() {
        return this.image;
    }

    public VolatileImage getVolatileImage() {
        return this.volatileImage;
    }

    public void setVolatileImage(VolatileImage volatileImage) {
        this.volatileImage = volatileImage;
    }

    public void setNoNetwork() {
        this.removeAll();
        this.add(this.buildOfflinePanel());
        this.validate();
    }

    public void setError(String error) {
        this.removeAll();
        this.add(this.buildLoginPanel());
        this.errorLabel.setText(error);
        this.validate();
    }
}
