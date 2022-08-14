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

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

public class MSAuthenticate {
    private final MSTokenRequests microsoftTokens = new MSTokenRequests(this);
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
    private static final String apiMinecraftStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";
    public final LauncherFrame launcherFrame;
    private final JFrame frame;
    private JDialog dialog;

    public MSAuthenticate(LauncherFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
        this.frame = new JFrame();
    }

    public void authenticate() {
        if (AuthLastLogin.readLastLogin() != null) {
                getMinecraftProfile(Objects.requireNonNull(AuthLastLogin.readLastLogin()).getAccessToken());
                return;
        }
        SwingUtilities.invokeLater(this::run);
    }

    private void run() {
        if (dialog != null) {
            dialog.toFront();
            return;
        }
        dialog = new JDialog(frame, Dialog.ModalityType.MODELESS);
        dialog.setTitle("Sign in to Minecraft");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog = null;
            }
        });
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
                    change.getAddedSubList().stream().filter(entry ->
                            entry.getUrl().startsWith(loaDesktopUrl + "?code=")).map(entry ->
                            entry.getUrl().substring(entry.getUrl().indexOf("=") + 1,
                                    entry.getUrl().indexOf("&"))).forEachOrdered(microsoftTokens::getAccessToken);
                }
                if (change.wasAdded() && webView.getEngine().getLocation().contains("oauth20_desktop.srf?error=access_denied")) {
                    dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                }
            });
            fxPanel.setScene(new Scene(webView));
        });
        try {
            dialog.setIconImage(ImageIO.read(Objects.requireNonNull(MSAuthenticate.this.getClass().getClassLoader().getResource("resources/favicon2.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ##################################################
     * #               GETTERS & SETTERS                #
     * ##################################################
     */
    public void getMinecraftStore(String accessToken) {
        try {
            MCUtils.requestMethod(apiMinecraftStoreUrl, "GET", accessToken);
        } catch (IOException e) {
            this.launcherFrame.showError("Login failed");
            this.launcherFrame.getAuthPanel().setNoNetwork();
        }
    }

    void getMinecraftProfile(String accessToken) {
        String username = AuthPanel.getUsernameTextField().getText();
        try {
            JSONObject apiResponse = MCUtils.requestMethod(apiMinecraftProfileUrl, "GET", accessToken);

            String name = apiResponse.getString("name");
            String uuid = apiResponse.getString("id");
            new AuthCredentials(accessToken, uuid);
            AuthLastLogin.writeLastLogin(AuthCredentials.credentials.getAccessToken(), AuthCredentials.credentials.getUuid());
            frame.dispose();
            System.out.println("Username is '" + username + "'");
            this.launcherFrame.getOnlineInstance(name, AuthCredentials.credentials.getAccessToken());
        } catch (IOException e) {
            if (e.getMessage().contains("api.minecraftservices.com")) {
                this.launcherFrame.showError("Can't connect to minecraft.net");
                this.launcherFrame.getAuthPanel().setNoNetwork();
                return;
            }
            if (e.getMessage().contains("401")) {
                SwingUtilities.invokeLater(this::run);
            }
            e.printStackTrace();
        }
    }
}
