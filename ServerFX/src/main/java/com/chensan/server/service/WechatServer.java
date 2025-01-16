package com.chensan.server.service;

import com.chensan.common.Message;
import com.chensan.common.MessageType;
import com.chensan.common.User;
import com.chensan.server.service.Thread.ManageSCCT;
import com.chensan.server.service.Thread.ReceiveThread;
import com.chensan.server.service.Thread.SendThread;
import com.chensan.server.utils.FileUtils;
import com.chensan.server.utils.NetUtils;
import com.chensan.server.wechatframe.ServerController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class WechatServer {
    private ServerSocket serverSocket = null;

    public static ConcurrentHashMap<String, User> getValidUser() {
        return validUser;
    }

    public static HashMap<String, String> managerUser = new HashMap<>();

    static {
        managerUser.put("114514", "114514");
    }

    public static boolean verifyManager(String userID, String password) {
        return managerUser.containsKey(userID) && managerUser.get(userID).equals(password);
    }

    //给定合法用户
    private static ConcurrentHashMap<String, User> validUser;//使用ConcurrentHashMap线程安全

    static {
        try {
            validUser = FileUtils.readUserFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        validUser.put("1", new User("1", "1"));
        validUser.put("2", new User("2", "2"));
        validUser.put("3", new User("3", "3"));
        validUser.put("4", new User("4", "4"));
    }

    public static void deleteUser(String userID) {
        validUser.remove(userID);
    }


    //消息管理

    private static ConcurrentHashMap<String, List<Message>> messageData;

    static {
        try {
            messageData = FileUtils.readMessageFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void MessageDataAdd(String ID, Message message) {
        if (messageData.containsKey(ID)) {
            messageData.get(ID).add(message);
        } else {
            List<Message> messageList = new LinkedList<>();
            messageList.add(message);
            messageData.put(ID, messageList);
        }
    }

    private static ConcurrentHashMap<String, HashMap<String, Message>> fileDataMap;

    static {
        try {
            fileDataMap = FileUtils.readFileMessageFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void fileDataAdd(String ID, Message message) {
        if (fileDataMap.containsKey(ID)) {
            fileDataMap.get(ID).put(message.getContent(), message);
        } else {
            HashMap<String, Message> messageData = new HashMap<>();
            messageData.put(message.getContent(), message);
            WechatServer.fileDataMap.put(ID, messageData);
        }
    }

    public static Message getFileMessage(String receiver, String fileName) {
        return fileDataMap.get(receiver).get(fileName);
    }

    public static void removeFileMessage(String receiver, String fileName) {
        fileDataMap.get(receiver).remove(fileName);
    }

    //在线消息暂存
    private static ConcurrentHashMap<String, BlockingQueue<Message>> onlineMessage = new ConcurrentHashMap<>();

    public static void storeOnlineMessage(String receiverID, Message message) {
        onlineMessage.get(receiverID).add(message);
    }

    public static ConcurrentHashMap<String, BlockingQueue<Message>> getOnlineMessage() {
        return onlineMessage;
    }

    //文件消息暂存
    public static void storeFileMessage(String receiverID, Message message) {
        fileDataAdd(receiverID, message);
    }

    //离线、在线消息判断
    public static void toSendMessage(Message message) {
        String receiverID = message.getReceiver();
        String senderID = message.getSender();
        if (!message.getMessageType().equals(MessageType.COMM_FILE)) {
            if (ManageSCCT.getSCCTMap().containsKey(receiverID)) {
                WechatServer.storeOnlineMessage(receiverID, message);
            }
            if (receiverID.equals(senderID)) {
                MessageDataAdd(receiverID, message);
            } else {
                MessageDataAdd(receiverID, message);
                MessageDataAdd(senderID, message);
            }
        } else {
            WechatServer.storeOnlineMessage(receiverID, message);
        }
    }

    public static void toSendAllMessage(Message message) {
        String senderID = message.getSender();
        for (String receiverID : validUser.keySet()) {
            message.setReceiver(receiverID);
            if (ManageSCCT.getSCCTMap().containsKey(receiverID) && !receiverID.equals(senderID)) {
                WechatServer.storeOnlineMessage(receiverID, message);
            }
            if (receiverID.equals(senderID)) {
                MessageDataAdd(senderID, message);
            } else {
                MessageDataAdd(receiverID, message);
            }

        }
    }


    //运行主程序
    public WechatServer() {
        System.out.println("服务端在9999端口等待客户端连接");
        try {
            serverSocket = new ServerSocket(9999);
            while (true) {
                Socket socket = serverSocket.accept();
                User user = NetUtils.receiveUser(socket);
                Message message = new Message();
                if (user.getType().equals("newUser")) {
                    if (validUser.containsKey(user.getUserID())) {
                        message.setMessageType(MessageType.REGISTER_FAILURE);
                        message.setContent("该账号已被注册");
                        message.setReceiver(user.getUserID());
                        NetUtils.sendMessage(socket, message);
                        System.out.println("<注册>" + user + "已存在，注册失败");
                        writeMessageDinary("<注册>" + user + "已存在，注册失败");
                    } else {
                        validUser.put(user.getUserID(), user);
                        System.out.println("<注册>新用户注册成功: " + user.getUserID());
                        writeMessageDinary("<注册>新用户注册成功: " + user.getUserID());
                        message.setMessageType(MessageType.REGISTER_SUCCESS);
                        message.setContent("账号注册成功！");
                        message.setReceiver(user.getUserID());
                        NetUtils.sendMessage(socket, message);
                    }
                } else {
                    if (WechatServer.verifyUser(user)) {
                        message.setMessageType(MessageType.LOGIN_SUCCESS);
                        NetUtils.sendMessage(socket, message);
                        SendThread sendThread = new SendThread(user.getUserID());
                        ReceiveThread receiveThread = new ReceiveThread(user.getUserID());
                        onlineMessage.put(user.getUserID(), new LinkedBlockingQueue<>());
                        ManageSCCT.addCCST(user.getUserID(), socket, sendThread, receiveThread);
                        System.out.println("<登录>用户" + user.getUserID() + "登录成功");
                        writeMessageDinary("<登录>用户" + user.getUserID() + "登录成功");
                        deliverMessages(user.getUserID());
                        receiveThread.start();
                        sendThread.start();
                    } else {
                        System.out.println("<登录>非法用户尝试登录");
                        writeMessageDinary("<登录>非法用户尝试登录");
                        message.setMessageType(MessageType.
                                LOGIN_FAILURE);
                        NetUtils.sendMessage(socket, message);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("用户登录异常");
            e.printStackTrace();
        }
    }

    //验证用户
    private static boolean verifyUser(User user) {
        if (validUser.containsKey(user.getUserID()) && validUser.get(user.getUserID())
                .getPassword().equals(user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    //等待消息队列清空
    public static boolean waitForQueueEmpty(String userId, long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        BlockingQueue<Message> userQueue = onlineMessage.get(userId);

        if (userQueue == null) {
            return true;
        }
        while (!userQueue.isEmpty()) {
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

    //关闭用户连接
    public static void shutdownServer(String userID) {
        for (String userId : onlineMessage.keySet()) {
            if (!waitForQueueEmpty(userId, 5000)) {
                System.err.println("用户 [" + userId + "] 消息队列未能及时清空");
            }
        }
        SendThread sendThread = ManageSCCT.getSendThread(userID);
        ReceiveThread receiveThread = ManageSCCT.getReceiveThread(userID);
        if (sendThread != null) sendThread.stopThread();
        if (receiveThread != null) receiveThread.stopThread();
        NetUtils.closeSocket(ManageSCCT.getSocket(userID));
        ManageSCCT.removeCCST(userID);
    }

    //关闭服务器（维护）
    public static void closeServer() throws Exception {
        Message message = new Message();
        message.setMessageType(MessageType.SERVER_CLOSE);
        for (String userID : ManageSCCT.getSCCTMap().keySet()) {
            Socket socket = ManageSCCT.getSocket(userID);
            NetUtils.sendMessage(socket, message);
            System.out.println("通知用户 [" + userID + "] 服务器关闭");
        }
        Thread.sleep(1000);
        FileUtils.writeMessageToFile(messageData);
        FileUtils.writeUserToFile(validUser);
        FileUtils.writeFileMessageToFile(fileDataMap);
    }

    public static void forcedExit(String userID) throws Exception {
        Message message = new Message();
        message.setMessageType(MessageType.FORCED_EXIT);
        Socket socket = ManageSCCT.getSocket(userID);
        NetUtils.sendMessage(socket, message);
        System.out.println("用户 [" + userID + "] 已被强制退出");
    }

    //传递历史聊天记录
    private static void deliverMessages(String userID) {
        if (messageData.containsKey(userID)) {
            List<Message> messages = new LinkedList<>(messageData.get(userID));
            System.out.println("用户 [" + userID + "] 有 " + messages.size() + " 条消息，正在投递...");
            Socket userSocket = ManageSCCT.getSocket(userID);
            while (!messages.isEmpty()) {
                Message message = messages.remove(0);
                NetUtils.sendMessage(userSocket, message);
                System.out.println("消息已发送给用户 [" + userID + "]: " +
                        message.getSender() + "->" + message.getReceiver() + ": " + message.getContent());
            }
        } else {
            System.out.println("用户 [" + userID + "] 没有消息需要投递");
        }
    }

    public static String getFriend() {
        StringBuilder friend = new StringBuilder();
        //validUser
        for (String userID : validUser.keySet()) {
            friend.append(userID).append(" ");
        }
        friend.append("\n");
        //onlineUser
        for (String userID : ManageSCCT.getSCCTMap().keySet()) {
            friend.append(userID).append(" ");
        }
        return friend.toString();
    }

    private final static String FILE_PATH = "C:\\Users\\honor\\Desktop\\WeChat\\ServerFX\\src\\main\\java\\com\\chensan\\server\\data\\MessageDinary";
    private static FileWriter writer;
    static {
        try {
            writer = new FileWriter(FILE_PATH, true);
        } catch (IOException e) {
        }
    }

    public static void writeMessageDinary(String time, String src) {
        try {
            writer.write("<" + time + ">" + src+"\n");
            writer.flush();
            System.out.println("文件记录成功");
        } catch (IOException e) {
            System.out.println("<日志>日志记录异常");
        }
        ServerController.getInstance().handleDinaryRecord("<" + time + ">" + src);
    }

    public static void writeMessageDinary(String src) {
        try {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String time = sdf.format(now);
            writer.write("<" + time + ">" + src+"\n");
            writer.flush();
            ServerController.getInstance().handleDinaryRecord("<" + time + ">" + src);
        } catch (IOException e) {
            System.out.println("<日志>写入异常");
        }
    }
}
