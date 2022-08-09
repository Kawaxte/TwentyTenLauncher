package net.minecraft.launcher.auth.microsoft;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import net.minecraft.MCUtils;
import net.minecraft.launcher.LauncherFrame;
import net.minecraft.launcher.auth.AuthCredentials;
import net.minecraft.launcher.auth.AuthLastLogin;
import net.minecraft.launcher.auth.AuthPanel;
import org.json.JSONObject;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

public class MSAuthenticate extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private final MSTokens microsoftTokens = new MSTokens(this);
    private static final String loaAuthUrl = "https://login.live.com/oauth20_authorize.srf"
            + "?client_id=00000000402b5328"
            + "&response_type=code"
            + "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL"
            + "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
    static final String loaDesktopUrl = "https://login.live.com/oauth20_desktop.srf";
    static final String loaTokenUrl = "https://login.live.com/oauth20_token.srf";
    static final String xblUserAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    static final String xblXstsAuthurl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    static final String apiMinecraftAuthUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String apiMinecraftProfileUrl = "https://api.minecraftservices.com/minecraft/profile";
    public final LauncherFrame launcherFrame;
    private final JFrame frame;
    private JDialog dialog;

    public MSAuthenticate(LauncherFrame launcherFrame, JFrame frame) {
        this.launcherFrame = launcherFrame;
        this.frame = frame;
    }

    public void authenticate() {
        if (AuthLastLogin.readLastLogin() != null) {
            if (!Objects.requireNonNull(AuthLastLogin.readLastLogin()).isValid()) {
                AuthLastLogin.deleteLastLogin();
                return;
            }
            acquireMCProfile(Objects.requireNonNull(AuthLastLogin.readLastLogin()).getAccessToken());
        } else {
            SwingUtilities.invokeLater(() -> actionPerformed(new ActionEvent(this, 0, "Authenticate")));
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (dialog != null) {
            dialog.toFront();
            return;
        }
        dialog = new JDialog(frame, JDialog.ModalityType.MODELESS);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog = null;
            }
        });
        dialog.setTitle("Sign in to Minecraft");
        JFXPanel fxPanel = new JFXPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(468, 634);
            }
        };
        dialog.add(fxPanel);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().load(loaAuthUrl);
            webView.getEngine().setJavaScriptEnabled(true);
            webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) change -> {
                if (change.next() && change.wasAdded()) {
                    for (WebHistory.Entry entry : change.getAddedSubList()) {
                        if (entry.getUrl().startsWith(loaDesktopUrl + "?code=")) {
                            String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                            microsoftTokens.acquireAccessToken(authCode);
                        }
                    }
                }
                if (change.wasAdded() && webView.getEngine().getLocation().contains("oauth20_desktop.srf?error=access_denied")) {
                    dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                    dialog = null;
                }
            });
            fxPanel.setScene(new Scene(webView));
        });
        dialog.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("favicon2.png"))).getImage());
    }

    void acquireMCProfile(String access_token) {
        String username = AuthPanel.getUsernameTextField().getText();
        String clientToken = this.launcherFrame.getClientToken();
        try {
            JSONObject apiResponse = MCUtils.requestMethod(apiMinecraftProfileUrl, "GET", access_token);
            if (apiResponse == null) {
                throw new IOException("No response from Minecraft API");
            }
            String name = apiResponse.getString("name");
            String uuid = apiResponse.getString("id");
            new AuthCredentials(username, clientToken, access_token, uuid);
            AuthLastLogin.writeLastLogin(AuthCredentials.credentials.getUsername(),
                    AuthCredentials.credentials.getClientToken(), AuthCredentials.credentials.getAccessToken(), AuthCredentials.credentials.getUuid());
            frame.dispose();
            System.out.println("Username is '" + username + "'");
            this.launcherFrame.getOnlineInstance(name, String.format("%s:%s:%s",
                    AuthCredentials.credentials.getClientToken(), AuthCredentials.credentials.getAccessToken(), AuthCredentials.credentials.getUuid()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
