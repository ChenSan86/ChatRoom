package com.chensan.client.service.Tread;

import com.chensan.client.service.MessageService;
import com.chensan.client.utils.NetUtils;
import com.chensan.common.Message;

import java.io.IOException;
import java.net.Socket;

public class SendThread extends Thread {
    private String userID;
    private Socket socket;
    private volatile boolean running = true;

    public SendThread(String userID) {
        this.userID = userID;
    }

    @Override
    public void run() {

        socket = ManageCCST.getSocket(userID);
        try {
            while (running && !socket.isClosed()) {
                try {
                    Message message = MessageService.getMessageQueue(userID).take();
                    if (message!=null) {
                        NetUtils.sendMessage(socket, message);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("<客户端>发送线程出错" + e.getMessage());
        }finally {
            NetUtils.closeSocket(socket);
        }
    }
    public static boolean waitForQueueEmpty(long timeoutMillis,String userID) {
        long startTime = System.currentTimeMillis();
        while (!MessageService.getMessageQueue(userID).isEmpty()) {
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }
    public void stopThread() {
        running = false;
        this.interrupt();
    }

}