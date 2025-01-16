package com.chensan.client.service;

import com.chensan.client.utils.StringUtils;
import com.chensan.common.Message;
import com.chensan.common.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
//用于处理消息的服务类
public class MessageService {
    protected static ConcurrentHashMap<String, BlockingQueue<Message>> MessageMap = new ConcurrentHashMap<>();
    public static BlockingQueue<Message> getMessageQueue(String ID) {
        return MessageMap.get(ID);
    }
    public static void sendFileToOne(String filename,String src, String sender, String receiver) {
        Message message = new Message();
        message.setMessageType(MessageType.COMM_FILE);
        message.setSender(sender);
        message.setSrc(src);
        message.setReceiver(receiver);
        message.setContent(filename);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = sdf.format(now);
        message.setSendTime();
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            message.setFileByte(fileBytes);
        } catch (Exception e) {
            System.out.println("读取文件失败！");
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
            }
        }
        MessageMap.get(sender).add(message);
    }

    public static void sendMessageToOne(String content, String sender, String receiver) {
        Message message = new Message();
        message.setMessageType(MessageType.COMM_MESSAGE);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSendTime();
        MessageMap.get(sender).add(message);
    }
    //发送广播消息
    public static void sendMessageToAll(String content, String sender) {
        Message message = new Message();
        message.setContent(content);
        message.setMessageType(MessageType.ALL_MESSAGE);
        message.setSendTime();
        message.setSender(sender);
        MessageMap.get(sender).add(message);
    }
    //获取在线好友名单
    public static void getFriends(String sender) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(sender);
        message.setMessageType(MessageType.GET_USER);
        MessageMap.get(sender).add(message);
    }
//展示接收到的文本消息
    public static void displayMessage(Message message) {
        String sender = message.getSender();
        String content = message.getContent();
        System.out.println("====================================");
        System.out.println(sender + ": " + content);
        System.out.println("====================================");
        //等待修改。。。。。。
    }
//显示在线用户
    public static void displayOnlineFriends(Message message) {
        String[] parts = message.getContent().split("\n");
        // 第一部分是合法用户ID
        String[] validUserIDs = parts[0].trim().split(" ");
        // 第二部分是在线用户ID，可能为空
        String[] onlineUserIDs = parts.length > 1 ? parts[1].trim().split(" ") : new String[0];
        // 打印结果
        System.out.println("Valid User IDs:");
        for (String id : validUserIDs) {
             // 检查ID是否不为空
                System.out.println("====================================");
                System.out.println(id);
                System.out.println("====================================");
        }
        System.out.println("\nOnline User IDs:");
        if (onlineUserIDs.length == 0){
            System.out.println("无在线用户！");
        }
        else {
            for (String id : onlineUserIDs) {
                // 检查ID是否不为空
                System.out.println("====================================");
                System.out.println(id);
                System.out.println("====================================");
            }
        }
    }
    public static void displayFileResult(Message message){
        System.out.println("====================================");
        System.out.println(message.getContent());
        System.out.println("====================================");
    }
    public static void replyFileEnquire(String key,Message message){
        Message reply = new Message();
        switch (key){
            case "Y":
                reply.setReceiver(message.getSender());
                reply.setSender(message.getReceiver());
                reply.setMessageType(MessageType.AGREE_FILE);
                System.out.println("请输入文件保存地址： ");
                String targetDesk  = StringUtils.readString(100);
                reply.setTargetDest(targetDesk);
                MessageMap.get(reply.getSender()).add(message);
                break;
            case "N":
                reply.setReceiver(message.getSender());
                reply.setSender(message.getReceiver());
                reply.setMessageType(MessageType.REFUSE_FILE);
                MessageMap.get(reply.getSender()).add(message);
                break;
        }
    }
    public static void receiveFile(Message message){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(message.getTargetDest());
            fileOutputStream.write(message.getFileByte());
            fileOutputStream.close();
            System.out.println("文件保存成功！");
        } catch (IOException e) {
            System.out.println("文件保存失败！" + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void sendMessage(Message message){
        String sender = message.getReceiver();
        MessageMap.get(sender).add(message);
    }
}
