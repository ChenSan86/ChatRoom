package com.chensan.client.viewFX;

import com.chensan.client.service.MessageService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;

public class FileSendController {
    private String userID;

    @FXML
    private TextField filePathField;

    @FXML
    private ListView<String> userListView;

    @FXML
    private Label dragAndDropArea;

    @FXML
    public void initialize() {
        MainController mainController = MainController.getInstance();
        ObservableList<String> contacts = FXCollections.observableArrayList(
                mainController.getAllContactsList().getItems()
        );
        userListView.setItems(contacts);
        userID = MainController.getInstance().userID;
        dragAndDropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != dragAndDropArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            event.consume();
        });
        dragAndDropArea.setOnDragDropped(event -> {
            var db = event.getDragboard();
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                filePathField.setText(file.getAbsolutePath());
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    @FXML
    private void chooseFile() {
        // 选择文件
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleSendFile() {
        // 获取文件路径和接收用户
        String filePath = filePathField.getText();
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        File file = new File(filePath);
        String fileName = file.getName();
        if (filePath.isEmpty() || selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请确保文件路径和接收用户已选择！");
            alert.showAndWait();
            return;
        }
        System.out.printf("文件 %s 正在发送给 %s...%n", filePath, selectedUser);
        MessageService.sendFileToOne(fileName, filePath,userID, selectedUser);
        filePathField.getScene().getWindow().hide();
    }
    @FXML
    private void cancel() {
        filePathField.getScene().getWindow().hide();
    }

}
