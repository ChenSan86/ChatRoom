package com.chensan.server.wechatframe;

import com.chensan.server.service.WechatServer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField idField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Label errorLabel;
    @FXML
    public void handleLogin() {
        String id = idField.getText();
        String pwd = pwdField.getText();
        if(WechatServer.verifyManager(id, pwd)){
            //让主界面显示
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
            Stage primaryStage = WeChatServerStart.getPrimaryStage();
            primaryStage.show();
        }
        else{
            errorLabel.setText("用户名或密码错误");
        }

    }
}
