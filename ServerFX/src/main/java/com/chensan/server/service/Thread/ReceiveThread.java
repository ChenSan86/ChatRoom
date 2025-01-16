package com.chensan.server.service.Thread;

import com.chensan.common.Message;
import com.chensan.common.MessageType;
import com.chensan.server.service.WechatServer;
import com.chensan.server.utils.NetUtils;

import java.net.Socket;

public class ReceiveThread extends Thread {

    private String userID;
    private Socket socket;
    private volatile boolean running = true;

    public ReceiveThread(String userID) {
        this.userID = userID;
    }

    @Override
    public void run() {
        try {
            socket = ManageSCCT.getSocket(userID);
            while (running) {
                try {
                    Message message = NetUtils.receiveMessage(socket);
                    switch (message.getMessageType()) {
                        case CLIENT_EXIT:
                            //退出胃信
                            System.out.println("<退出>"+userID + "已退出胃信");
                            WechatServer.writeMessageDinary("<退出>"+userID + "已退出胃信");
                            WechatServer.shutdownServer(message.getSender());
                            break;
                            //消息处理区
                        case ALL_MESSAGE:
                            WechatServer.toSendAllMessage(message);
                            System.out.println("<消息>"+message.getSender() + "->群聊："+ message.getContent());
                            WechatServer.writeMessageDinary(message.getSendTime(),
                                    "<消息>"+message.getSender() + "->群聊："+ message.getContent());
                            break;
                        case GET_USER,COMM_MESSAGE:
                            WechatServer.toSendMessage(message);
                            if(message.getMessageType() == MessageType.COMM_MESSAGE){
                                System.out.println("<消息>"+message.getSender() + "->"+
                                        message.getReceiver() + "："+ message.getContent());
                                WechatServer.writeMessageDinary(message.getSendTime(),
                                        "<消息>"+message.getSender() + "->"+
                                        message.getReceiver() + "："+ message.getContent());
                            }
                            break;
                            //文件处理区
                        case COMM_FILE:
                            System.out.println("<文件>接收到文件发送请求"+message.getSender()+
                                    "->"+message.getReceiver());
                            WechatServer.writeMessageDinary(message.getSendTime(),
                                    "<文件>接收到文件发送请求"+message.getSender()+
                                    "->"+message.getReceiver());
                            Message message1 = new Message();
                            message1.setMessageType(MessageType.FILE_ENQUIRE);
                            message1.setSender(message.getSender());
                            message1.setReceiver(message.getReceiver());
                            message1.setContent(message.getContent());
                            message1.setSendTime(message.getSendTime());
                            WechatServer.toSendMessage(message1);
                            WechatServer.storeFileMessage(message.getReceiver(),message);
                            System.out.println("<文件>发送文件询问完成->"+message.getReceiver());
                            WechatServer.writeMessageDinary(
                                    "<文件>发送文件询问完成->"+message.getReceiver());
                            break;
                        case AGREE_FILE:
                            //同意接收文件
                            System.out.println("<文件>"+message.getReceiver()+"同意接收文件");
                            WechatServer.writeMessageDinary("<文件>"+message.getReceiver()+"同意接收文件");
                            String fileName = message.getContent();
                            String receiverID = message.getReceiver();
                            Message message3 = WechatServer.getFileMessage(receiverID,fileName);
                            message3.setTargetDest(message.getTargetDest());
                            WechatServer.toSendMessage(message3);
                            break;
                        case REFUSE_FILE:
                            //拒绝接收文件
                            System.out.println("<文件>"+message.getReceiver()+"拒绝接收文件");
                            WechatServer.writeMessageDinary("<文件>"+message.getReceiver()+"拒绝接收文件");
                            WechatServer.removeFileMessage(message.getReceiver(),message.getContent());
                            break;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("<服务器>接收线程出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void stopThread() {
        running = false;
        this.interrupt();
    }
}