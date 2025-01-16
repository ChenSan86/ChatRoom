package com.chensan.client.viewFX;

import com.chensan.client.service.MessageService;
import com.chensan.client.service.UserService;
import com.chensan.common.Message;
import com.chensan.common.MessageType;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainController {
    private static MainController instance;
    public String userID;

    @FXML
    private ListView<String> allContactsList;

    public ListView<String> getAllContactsList() {
        return allContactsList;
    }

    @FXML
    private ListView<String> onlineContactsList;

    @FXML
    private ListView<String> groupChatList;

    @FXML
    private TabPane chatTabPane;

    @FXML
    private TextField messageInput;

    @FXML
    private Label contactNameLabel;
    private final Map<String, Tab> contactTabs = new HashMap<>();

    private List<Message> fileMessages;

    public List<Message> getFileMessages() {
        return new LinkedList<Message>(fileMessages);
    }

    public void AddFileMessages(Message message) {
        fileMessages.add(message);
    }

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }


    public void setMainID(String ID) {
        this.userID = ID;
    }

    @FXML
    public void initialize() {

        allContactsList.getItems().addAll("1", "2", "3", "4");

        groupChatList.getItems().addAll("群聊");

        getInstance();
        fileMessages = new LinkedList<>();
    }

    @FXML
    public void logout() {
        //跳转登录界面
        try {
            UserService userService = new UserService(userID);
            userService.exit();
            instance = null;
            contactTabs.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage mainStage = (Stage) allContactsList.getScene().getWindow();
            mainStage.close();
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("登录");
            loginStage.setWidth(800);
            loginStage.setHeight(600);
            loginStage.setResizable(false);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendMessage() {
        //发送消息（发送按钮）
        String content = messageInput.getText();
        if (!content.isEmpty()) {
            Tab selectedTab = chatTabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                ScrollPane scrollPane = (ScrollPane) selectedTab.getContent();
                VBox chatBox = (VBox) scrollPane.getContent();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date currentDate = new Date();
                String formattedDate = formatter.format(currentDate);
                Text messageText = new Text("<"+formattedDate+">"+"\n"+"我: " + content);
                chatBox.getChildren().add(messageText);
                messageInput.clear();
                String contact = selectedTab.getText();
                Message ms = new Message();
                ms.setSender(userID);
                if (!"群聊".equals(contact)) {
                    MessageService.sendMessageToOne(content, userID, contact);
                } else {
                    MessageService.sendMessageToAll(content, userID);
                }
            }
        }
    }

    @FXML
    public void handleReceiveMessage(Message message) {
        //接收消息显示
        Platform.runLater(() -> {
            String sender = message.getSender();
            String content = message.getContent();
            String receiver = message.getReceiver();
            String sendTime = message.getSendTime();
            if (message.getMessageType() == MessageType.COMM_MESSAGE) {
                Tab tab = null;
                if (sender.equals(userID)) {
                    tab = getTab(receiver);
                } else {
                    tab = getTab(sender);
                }
                ScrollPane scrollPane = (ScrollPane) tab.getContent();
                VBox chatBox = (VBox) scrollPane.getContent();
                Text messageText = null;
                if (sender.equals(userID)) {
                    messageText = new Text("<"+sendTime+">"+"\n"+"我: " + content);
                } else {
                    messageText = new Text("<"+sendTime+">"+"\n"+sender + ": " + content);
                }
                chatBox.getChildren().add(messageText);
            }
            if (message.getMessageType() == MessageType.ALL_MESSAGE) {
                Tab tab = getTab("群聊");
                ScrollPane scrollPane = (ScrollPane) tab.getContent();
                VBox chatBox = (VBox) scrollPane.getContent();
                Text messageText = new Text("<"+sendTime+">"+"\n"+sender+ ": " + content);
                chatBox.getChildren().add(messageText);
            }
        });
    }

    @FXML
    private void handleContactSelection() {
        //选中收信对象
        String selectedContact = getSelectedContact();
        if (selectedContact != null && !selectedContact.contains("群聊")) {
            contactNameLabel.setText("与 " + selectedContact + " 聊天中");
            openChatTab(selectedContact);
        } else {
            contactNameLabel.setText(selectedContact);
            openChatTab(selectedContact);
        }
    }

    private String getSelectedContact() {
        //选中事件
        if (allContactsList.getSelectionModel().getSelectedItem() != null) {
            return allContactsList.getSelectionModel().getSelectedItem();
        }
        if (onlineContactsList.getSelectionModel().getSelectedItem() != null) {
            return onlineContactsList.getSelectionModel().getSelectedItem();
        }
        if (groupChatList.getSelectionModel().getSelectedItem() != null) {
            return groupChatList.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    private Tab getTab(String contact) {
        if (contactTabs.containsKey(contact)) {
            Tab existingTab = contactTabs.get(contact);
            return existingTab;
        } else {
            Tab newTab = new Tab(contact);
            newTab.setClosable(false);
            VBox chatBox = new VBox();
            chatBox.setSpacing(10);
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(chatBox);
            newTab.setContent(scrollPane);
            chatTabPane.getTabs().add(newTab);
            contactTabs.put(contact, newTab);
            return newTab;
        }
    }

    private void openChatTab(String contact) {
        Tab tab = getTab(contact);
        chatTabPane.getSelectionModel().select(tab);
    }

    @FXML
    private void handleRefresh() {
        //更新联系人列表
        MessageService.getFriends(userID);
    }

    @FXML
    public void refreshOnlineUsers(Message message) {
        //刷新在线用户列表
        Platform.runLater(() -> {
            String[] parts = message.getContent().split("\n");
            String[] validUserIDs = parts[0].trim().split(" ");
            String[] onlineUserIDs = parts.length > 1 ? parts[1].trim().split(" ") : new String[0];
            allContactsList.getItems().clear();
            allContactsList.getItems().addAll(validUserIDs);
            if (onlineUserIDs.length > 0) {
                onlineContactsList.getItems().clear();
                onlineContactsList.getItems().addAll(onlineUserIDs);
            }
        });
    }

    @FXML
    public void handleSendFile() throws IOException {
        //跳转发送文件界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/SendFile.fxml"));
        Scene scene = new Scene(loader.load());
        Stage sendFileStage = new Stage();
        sendFileStage.setScene(scene);
        sendFileStage.setTitle("发送文件");
        sendFileStage.show();
    }

    @FXML
    public void handleReceiveFile() throws IOException {
        //跳转接收文件界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/clientfx/ReceiveFile.fxml"));
        Scene scene = new Scene(loader.load());
        Stage receiveFileStage = new Stage();
        receiveFileStage.setHeight(600);
        receiveFileStage.setWidth(800);
        receiveFileStage.setScene(scene);
        receiveFileStage.setTitle("接收文件");
        receiveFileStage.show();
    }

    @FXML
    public void serverClose() {
        //处理服务器维护事件
        UserService userService = new UserService(userID);
        userService.exit();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "服务器维护，您已被强制下线", ButtonType.YES);
            alert.initOwner(chatTabPane.getScene().getWindow()); // 设置对话框的拥有者为当前Stage
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/clientfx/AlertStyle.css").toExternalForm());
            alert.getDialogPane().setId("custom-style");
            Set<Node> buttons = alert.getDialogPane().lookupAll(".button");
            for (Node node : buttons) {
                ((Button) node).setId("alert-button");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    Platform.exit();
                    System.exit(0);
                } else {
                }
            });
        });
    }

    @FXML
    public void forcedExit() {
        //处理强制退出事件
        UserService userService = new UserService(userID);
        userService.exit();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "您已被服务器强制退出程序", ButtonType.YES);
            alert.initOwner(chatTabPane.getScene().getWindow());
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/clientfx/AlertStyle.css").toExternalForm());
            alert.getDialogPane().setId("custom-style");
            Set<Node> buttons = alert.getDialogPane().lookupAll(".button");
            for (Node node : buttons) {
                ((Button) node).setId("alert-button");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    Platform.exit();
                    System.exit(0);
                } else {
                }
            });
        });
    }
}


