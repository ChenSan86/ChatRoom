package com.chensan.client.viewFX;

import com.chensan.client.service.MessageService;
import com.chensan.common.Message;
import com.chensan.common.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.List;

public class FileReceiveController {
    @FXML
    private TableView<Message> fileTable;
    private Message getSelectedFile() {
        //获取选中文件
        SelectionModel<Message> selectionModel = fileTable.getSelectionModel();
        if (selectionModel.getSelectedItem() != null) {
            return selectionModel.getSelectedItem();
        }
        return null;
    }

    @FXML
    private void initialize() {
        MainController mainController = MainController.getInstance();
        List<Message> messages = mainController.getFileMessages();
        ObservableList<Message> observableMessages = FXCollections.observableList(messages);
        fileTable.setItems(observableMessages);
    }

    @FXML
    private void handleAccept() {
        //保存文件到指定文件夹
        Message message = getSelectedFile();
        String filePath = chooseFolder().concat("\\\\" + message.getContent());
        if (filePath == null) {
            return;
        }
        message.setTargetDest(filePath);
        message.setMessageType(MessageType.AGREE_FILE);
        MessageService.sendMessage(message);
        fileTable.getItems().remove(message);
    }

    @FXML
    private void handleReject() {
        //处理拒收文件
        Message message = getSelectedFile();
        message.setMessageType(MessageType.REFUSE_FILE);
        MessageService.sendMessage(message);
        fileTable.getItems().remove(message);
    }

    @FXML
    private void handleAcceptAll() {
        String filePath = chooseFolder();
        if (filePath == null) {
            return;
        }
        for (Message message : fileTable.getItems()) {
            filePath = filePath.concat("\\\\" + message.getContent());
            message.setMessageType(MessageType.AGREE_FILE);
            message.setTargetDest(filePath);
            MessageService.sendMessage(message);
            fileTable.getItems().remove(message);
        }
    }

    @FXML
    private void handleRejectAll() {
        for (Message message : fileTable.getItems()) {
            message.setMessageType(MessageType.REFUSE_FILE);
            MessageService.sendMessage(message);
            fileTable.getItems().remove(message);
        }
    }

    @FXML
    private String chooseFolder() {
        //选择文件夹
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件夹");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File folder = directoryChooser.showDialog(null);
        if (folder != null) {
            System.out.println(folder.getAbsolutePath());
            return folder.getAbsolutePath().replace("\\", "\\\\");
        }
        return null;
    }
}
