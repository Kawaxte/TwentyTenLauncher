package net.minecraft.auth.microsoft;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import net.minecraft.launcher.LFrame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

public class MAuthenticate extends VBox {
    private final MTokens minecraftTokens = new MTokens(this);
    private final MMCProfile minecraftProfile = new MMCProfile(this);

    private static final String authUrl = "https://login.live.com/oauth20_authorize.srf"
            + "?client_id=00000000402b5328"
            + "&response_type=code"
            + "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL"
            + "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";
    static final String redirectUrl = "https://login.live.com/oauth20_desktop.srf";
    static final String tokenUrl = "https://login.live.com/oauth20_token.srf";
    static final String xboxAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    static final String minecraftLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    static final String minecraftProfileUrl = "https://api.minecraftservices.com/minecraft/profile";
    public final LFrame launcherFrame;

    JFrame frame;
    WebView webView;

    public MAuthenticate(LFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    void reset() {
        if (webView != null) {
            this.getChildren().remove(webView);
        }

        MAuthenticate microsoftAuthenticate = this;

        Platform.runLater(() -> {
            webView = new WebView();
            microsoftAuthenticate.getChildren().add(webView);
            webView.getEngine().load(authUrl);
            webView.getEngine().setJavaScriptEnabled(true);
            webView.setPrefSize(485, 638);
            webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
                if (c.next() && c.wasAdded()) {
                    for (WebHistory.Entry entry : c.getAddedSubList()) {
                        if (entry.getUrl().startsWith(redirectUrl + "?code=")) {
                            minecraftTokens.acquireAccessToken(entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&")));
                        }
                    }
                }

                if (c.wasAdded() && webView.getEngine().getLocation().contains("oauth20_desktop.srf?error=access_denied")) {
                    frame.dispose();
                }
            });
        });
    }

    public void authenticate() {
        try {
            frame = new JFrame("Login with Microsoft");
            JFXPanel jfxPanel = new JFXPanel();
            frame.add(jfxPanel);
            frame.pack();
            frame.setVisible(true);
            Platform.runLater(() -> jfxPanel.setScene(new Scene(this)));
            frame.setSize(485, 638);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("favicon2.png"))).getImage());
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    new Thread(() -> Platform.runLater(() -> {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        frame.dispose();
                    })).start();
                }
            });
            reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String ofFormData(Map<Object, Object> parameter) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : parameter.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public JFrame getFrame() {
        return frame;
    }

    public LFrame getLauncherFrame() {
        return launcherFrame;
    }

    public void getMCProfile(String accessToken) {
        minecraftProfile.acquireMCProfile(accessToken);
    }
}