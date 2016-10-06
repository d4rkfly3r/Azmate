package net.d4rkfly3r.irc.azmate.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.d4rkfly3r.irc.azmate.Azmate;
import net.d4rkfly3r.irc.azmate.events.WindowCloseEvent;
import net.d4rkfly3r.irc.azmate.plugins.PluginBus;
import netscape.javascript.JSObject;

import javax.swing.*;

public class MainApplication extends Application {

    private final WebView webView;

    public MainApplication() {
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(true);
        this.webView.getEngine().setOnAlert(event -> JOptionPane.showMessageDialog(null, event.getData()));
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("java", this);
        });
        webView.getEngine().load(Azmate.class.getResource("/app.html").toExternalForm());

        VBox.setVgrow(webView, Priority.ALWAYS);
        HBox.setHgrow(webView, Priority.ALWAYS);
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(true);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMaximized(true);

        VBox pane = new VBox();
        pane.setMinSize(primaryStage.getWidth(), primaryStage.getHeight());


        BorderPane toolBar = new BorderPane();
        toolBar.setPrefHeight(30);
        toolBar.setPrefWidth(primaryStage.getWidth());

        Label title = new Label("Azmate");
        title.setFont(Font.loadFont(Azmate.class.getResource("/fonts/HeadlineNEWS.ttf").toExternalForm(), 26));
        title.getStyleClass().add("title");

        HBox controls = new HBox();
        final Label closeButton = FontAwesome.createIcon(FontAwesome.REMOVE, false);
        closeButton.setOnMouseClicked(event -> this.quit());
        final Label minimizeButton = FontAwesome.createIcon(FontAwesome.MINUS, true);
        minimizeButton.setOnMouseClicked(event -> primaryStage.setIconified(true));
        controls.getChildren().addAll(minimizeButton, closeButton);

        toolBar.setCenter(title);
        toolBar.setRight(controls);

        pane.getChildren().addAll(toolBar, this.webView);
        Scene root = new Scene(pane);
        root.getStylesheets().add(Azmate.class.getResource("/css/java.css").toExternalForm());
        primaryStage.setScene(root);
        primaryStage.show();
    }

    public void quit() {
        PluginBus.getInstance().fireEvent(new WindowCloseEvent());
        Platform.exit();
    }
}
