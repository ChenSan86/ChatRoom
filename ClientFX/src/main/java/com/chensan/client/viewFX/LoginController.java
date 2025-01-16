package com.chensan.client.viewFX;

import com.chensan.client.service.MessageService;
import com.chensan.client.service.UserService;
import com.chensan.client.utils.StringUtils;
import com.chensan.common.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Set;

public class LoginController {
    @FXML
    private TextField idField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Label errorLabel;

    private UserService userService = new UserService();

    @FXML
    public void handleLogin() {
        //校验用户
        String ID = idField.getText();
        String pwd = pwdField.getText();
        if (userService.checkUser(ID, pwd)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/Main.fxml"));
                Scene scene = new Scene(loader.load());
                Stage loginStage = (Stage) idField.getScene().getWindow();
                Stage mainStage = new Stage();
                mainStage.setScene(scene);
                mainStage.setTitle("用户："+ID);
                mainStage.setWidth(800);
                mainStage.setHeight(600);
                mainStage.show();
                MainController controller = loader.getController();
                controller.setMainID(ID);
                mainStage.setOnCloseRequest(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "您确定要退出程序吗？", ButtonType.YES, ButtonType.NO);
                    alert.initOwner(mainStage);
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/clientfx/AlertStyle.css").toExternalForm());
                    alert.getDialogPane().setId("custom-style");
                    Set<Node> buttons = alert.getDialogPane().lookupAll(".button");
                    for (Node node : buttons) {
                        ((Button) node).setId("alert-button");
                    }

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            event.consume();
                            UserService userService = new UserService(ID);
                            userService.exit();
                            Platform.exit();
                        } else {
                            event.consume();
                        }
                    });
                });
                loginStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("账号或密码错误！");
        }
    }

    @FXML
    public void showRegister() {
        //跳转注册界面
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/Register.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
            Stage registerStage = new Stage();
            registerStage.setScene(scene);
            registerStage.setTitle("注册");
            registerStage.setWidth(800);
            registerStage.setHeight(600);
            registerStage.setResizable(false);
            registerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
