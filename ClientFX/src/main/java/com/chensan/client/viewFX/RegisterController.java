package com.chensan.client.viewFX;

import com.chensan.client.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField idField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();

    @FXML
    public void handleRegister() {
        //校验注册
        String id = idField.getText();
        String pwd = pwdField.getText();
        if (userService.newUser(id, pwd)) {
            messageLabel.setText("用户注册成功！");
        } else {
            messageLabel.setText("该用户名已被注册！");
        }
    }

    @FXML
    public void showLogin() {
        //跳转到登录界面
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("登录");
            loginStage.setResizable(false);
            loginStage.setWidth(800);
            loginStage.setHeight(600);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
