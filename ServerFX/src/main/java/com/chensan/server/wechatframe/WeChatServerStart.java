package com.chensan.server.wechatframe;

import com.chensan.server.service.WechatServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Set;

public class WeChatServerStart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new Thread(() ->new WechatServer()).start();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/Server.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("服务器端");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "请点击“关闭服务器”按钮关闭服务器", ButtonType.YES);
            alert.initOwner(primaryStage);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/server/AlertStyle.css").toExternalForm());
            alert.getDialogPane().setId("custom-style");
            Set<Node> buttons = alert.getDialogPane().lookupAll(".button");
            for (Node node : buttons) {
                ((Button)node).setId("alert-button");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    event.consume();
                } else {
                    event.consume();
                }
            });
        });
        primaryStage.hide();

        this.primaryStage = primaryStage;
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/server/Login.fxml"));
        Scene loginScene = new Scene(loginLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("登录");
        loginStage.setScene(loginScene);
        loginStage.setWidth(800);
        loginStage.setHeight(600);
        loginStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "登录后才能关闭服务器", ButtonType.YES, ButtonType.NO);
            alert.initOwner(loginStage);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/server/AlertStyle.css").toExternalForm());
            alert.getDialogPane().setId("custom-style");
            Set<Node> buttons = alert.getDialogPane().lookupAll(".button");
            for (Node node : buttons) {
                ((Button)node).setId("alert-button");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    event.consume();
                } else {
                    event.consume();
                }
            });
        });
        loginStage.show();

        this.loginStage = loginStage;
    }
    private static Stage primaryStage;
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    private static Stage loginStage;
    public static Stage getLoginStage() {
        return loginStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
