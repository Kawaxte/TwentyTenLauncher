package net.minecraft.auth.microsoft;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import net.minecraft.auth.AUtils;
import net.minecraft.launcher.LFrame;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class MAuthenticate extends VBox {
    private final MTokens minecraftTokens = new MTokens(this);
    static final String authUrl = "https://login.live.com/oauth20_authorize.srf"
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
    static String clientTokenString;
    public final LFrame launcherFrame;
    final JFrame frame = new JFrame("Login with Microsoft");

    public MAuthenticate(LFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    public void authenticate() {
        frame.setVisible(true);
        frame.setPreferredSize(new Dimension(485, 638));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(launcherFrame);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                JFXPanel fxPanel = new JFXPanel();
                frame.add(fxPanel);

                Platform.runLater(() -> {
                    WebView webView = new WebView();
                    webView.getEngine().load(authUrl);
                    webView.getEngine().setJavaScriptEnabled(true);
                    webView.setPrefSize(485, 638);
                    webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) change -> {
                        if (change.next() && change.wasAdded()) {
                            for (WebHistory.Entry entry : change.getAddedSubList()) {
                                if (entry.getUrl().startsWith(redirectUrl + "?code=")) {
                                    clientTokenString = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                                    minecraftTokens.acquireAccessToken(clientTokenString);
                                }
                            }
                        }
                        if (change.wasAdded() && webView.getEngine().getLocation().contains("oauth20_desktop.srf?error=access_denied")) {
                            frame.dispose();
                        }
                    });
                    fxPanel.setScene(new Scene(webView));
                });
            }

            @Override
            public void windowClosing(WindowEvent e) {
                Platform.runLater(frame::dispose);
            }
        });
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("favicon2.png"))).getImage());
    }

    void acquireMCProfile(String accessToken) {
        try {
            JSONObject jsonResponse = AUtils.requestJSONGET(MAuthenticate.minecraftProfileUrl, accessToken);
            this.getLauncherFrame().playOnline(jsonResponse.getString("name"),
                    String.format("%s:%s:%s", clientTokenString.substring(9).replaceAll("-", ""), accessToken, jsonResponse.getString("id")));
            frame.dispose();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LFrame getLauncherFrame() {
        return launcherFrame;
    }
}