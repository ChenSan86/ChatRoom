package com.chensan.client.service.Tread;

import com.chensan.client.service.MessageService;
import com.chensan.client.service.UserService;
import com.chensan.client.utils.NetUtils;
import com.chensan.client.utils.StringUtils;
import com.chensan.client.viewFX.MainController;
import com.chensan.common.Message;

import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveThread extends Thread {

    private String userID;
    private Socket socket;
    private volatile boolean running = true;
    public ReceiveThread(String userID){
        this.userID = userID;
    }
    private UserService userService = new UserService();

    @Override
    public void run() {
        try {socket = ManageCCST.getSocket(userID);
            Thread.sleep(100);
            while (running && !socket.isClosed()) {
                try {
                    Message message = NetUtils.receiveMessage(socket);
                    if(message != null) {
                        switch (message.getMessageType()) {
                            case ALL_MESSAGE, COMM_MESSAGE:
                                MessageService.displayMessage(message);
                                MainController.getInstance().handleReceiveMessage(message);
                                break;
                            case RET_USER:
                                MessageService.displayOnlineFriends(message);
                                MainController.getInstance().refreshOnlineUsers(message);
                                break;
                            case FILE_ENQUIRE:
                                System.out.println(message.getSender()+"给你发来了文件，是否接收文件?");
                                MainController.getInstance().AddFileMessages(message);
                                break;
                            case COMM_FILE:
                                MessageService.receiveFile(message);
                                break;
                            case SERVER_CLOSE:
                                MainController.getInstance().serverClose();
                                break;
                            case FORCED_EXIT:
                                MainController.getInstance().forcedExit();
                                break;
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("<客户端>接收线程出错: " + e.getMessage());
        }
    }
    public void stopThread() {
        running = false;
        this.interrupt();
    }
}