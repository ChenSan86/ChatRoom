package com.chensan.server.service.Thread;

import com.chensan.common.Message;
import com.chensan.common.MessageType;
import com.chensan.common.User;
import com.chensan.server.service.WechatServer;
import com.chensan.server.utils.NetUtils;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class SendThread extends Thread {
    private String userID;
    private Socket socket;

    public SendThread(String userID) {
        this.userID = userID;
    }
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            socket = ManageSCCT.getSocket(userID);
            while (running) {
                try {
                    Message message = WechatServer.getOnlineMessage().get(userID).take();
                    if(message != null) {
                        switch (message.getMessageType()) {
                            //文本消息
                            case COMM_MESSAGE, ALL_MESSAGE,FILE_ENQUIRE,COMM_FILE:
                                NetUtils.sendMessage(ManageSCCT.getSocket(userID), message);
                                break;
                                //获取在线用户
                            case GET_USER:
                                message.setMessageType(MessageType.RET_USER);
                                message.setContent(WechatServer.getFriend());
                                NetUtils.sendMessage(socket, message);
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("<服务器>发送线程出错" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void stopThread() {
        running = false;
    }
    public static boolean waitForQueueEmpty(String userId, long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        BlockingQueue<Message> userQueue = WechatServer.getOnlineMessage().get(userId);

        if (userQueue == null) {
            return true; // 用户队列不存在，视为已清空
        }

        while (!userQueue.isEmpty()) {
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                return false; // 超时返回
            }
            try {
                Thread.sleep(50); // 每隔 50ms 检查一次
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复线程中断状态
                return false;
            }
        }
        return true; // 队列已清空
    }
}