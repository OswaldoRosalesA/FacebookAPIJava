/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.facebook.browser;

import api.facebook.Facebook;
import api.facebook.URLs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author oswaldo
 */
public class Browser extends JDialog {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();
    private final JProgressBar progressBar = new JProgressBar();
    private String code;
    public static final String PROP_CODE = "code";

    public Browser(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Browser.this.setTitle("Inicio de sesion con Facebook");
    }

    /**
     *
     * @param parent
     * @param appId
     * @param scope
     * @param redirec
     * @return
     */
    public static Optional<String> open(Frame parent, final String appId, final String scope, final String redirec) {
        Browser browser = new Browser(parent, true);
        browser.loadURL(URLs.logInURL(appId, redirec, scope));
        browser.setLocationRelativeTo(parent);
        browser.setVisible(true);
        return Optional.ofNullable(browser.getCode());
    }

    private void initComponents() {
        createScene();

        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);

        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);

        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setPreferredSize(new Dimension(1280, 720));
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void createScene() {

        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();

            engine.setOnStatusChanged((final WebEvent<String> event) -> {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(event.getData());
                });
            });

            engine.getLoadWorker().workDoneProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(newValue.intValue());
                });
            });

            engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                        if (newValue != Worker.State.SUCCEEDED) {
                            return;
                        }
                        String myUrl = engine.getLocation();
                        if ("https://www.facebook.com/dialog/close".equals(myUrl)) {
                            this.dispose();
                        }
                        if (myUrl.startsWith(Facebook.REDITECT_URL)) {
                            setCode(myUrl.substring(myUrl.indexOf("code=") + "code=".length()));
                            this.dispose();
                        }
                    });

            jfxPanel.setScene(new Scene(view));
        });
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void loadURL(final String url) {
        Platform.runLater(() -> {
            String tmp = toURL(url);
            if (tmp == null) {
                tmp = toURL("http://" + url);
            }
            engine.load(tmp);
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

}
