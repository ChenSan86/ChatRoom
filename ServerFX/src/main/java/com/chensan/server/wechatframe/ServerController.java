package com.chensan.server.wechatframe;

import com.chensan.server.service.Thread.ManageSCCT;
import com.chensan.server.service.WechatServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class ServerController {
    private static ServerController instance;
    public ServerController() {
        instance = this;
    }
    public static ServerController getInstance() {
        return instance;
    }
    @FXML
    private ListView<String> allContactsList;
    @FXML
    private ListView<String> onlineContactsList;
    @FXML
    private VBox dinaryBox;

    @FXML
    private void initialize() {
        allContactsList.getItems().addAll(WechatServer.getValidUser().keySet());
        onlineContactsList.getItems().addAll(ManageSCCT.getSCCTMap().keySet());
        loadHistoryLogs();
    }
    private void loadHistoryLogs() {
        String filePath = "C:\\Users\\honor\\Desktop\\WeChat\\ServerFX\\src\\main\\java\\com\\chensan\\server\\data\\MessageDinary"; // 假设日志文件的路径
        try {
            Files.lines(Paths.get(filePath))
                    .forEach(line -> {
                        Platform.runLater(() -> {
                            Text text = new Text(line);
                            dinaryBox.getChildren().add(text);
                        });
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void closeServer(){

            System.out.println("<服务器>服务器关闭");
            WechatServer.writeMessageDinary("<服务器>服务器关闭");
            try {
                WechatServer.closeServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Platform.exit();
            System.exit(0);

    }
    @FXML
    private void forcedExit(){
        String userID = getSelectedContact();
        System.out.println("<服务器>强制"+userID+"退出");
        WechatServer.writeMessageDinary("<服务器>强制"+userID+"退出");
        try {
            WechatServer.forcedExit(userID);
        } catch (Exception e) {
            System.out.println("<退出>用户强制退出失败："+e.getMessage());
            WechatServer.writeMessageDinary("<退出>用户强制退出失败");
        }
        refresh();
    }
    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) allContactsList.getScene().getWindow();
        stage.hide();
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
    }
    @FXML
    private void deleteUser(){
        String userID = getSelectedContact();
        System.out.println("<服务器>删除用户"+userID);
        WechatServer.writeMessageDinary("<服务器>删除用户"+userID);
        if(ManageSCCT.getSCCTMap().containsKey(userID)){
            try {
                WechatServer.forcedExit(userID);
            } catch (Exception e) {
                System.out.println("用户强制退出失败："+e.getMessage());
            }
        }
        WechatServer.deleteUser(userID);
        refresh();
    }
    @FXML
    private void refresh(){
        allContactsList.getItems().clear();
        onlineContactsList.getItems().clear();
        allContactsList.getItems().addAll(WechatServer.getValidUser().keySet());
        onlineContactsList.getItems().addAll(ManageSCCT.getSCCTMap().keySet());
    }
    private String getSelectedContact() {
        if (allContactsList.getSelectionModel().getSelectedItem() != null) {
            return allContactsList.getSelectionModel().getSelectedItem();
        }
        if (onlineContactsList.getSelectionModel().getSelectedItem() != null) {
            return onlineContactsList.getSelectionModel().getSelectedItem();
        }
        return null;
    }
    @FXML
    private void handleContactSelection() {}
    @FXML
    public void handleDinaryRecord(String message) {
        Platform.runLater(() -> {
            Text text = new Text(message);
            dinaryBox.getChildren().add(text);
        });
    }
}
