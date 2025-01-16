package com.chensan.client.viewFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewStartFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //登录界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/Login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("用户登录系统");
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}