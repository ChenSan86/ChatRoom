package com.chensan.client.service;

import com.chensan.client.service.Tread.ManageCCST;
import com.chensan.client.service.Tread.ReceiveThread;
import com.chensan.client.service.Tread.SendThread;
import com.chensan.client.utils.NetUtils;
import com.chensan.common.Message;
import com.chensan.common.MessageType;
import com.chensan.common.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
//用于处理用户相关的操作，包括登录、注册、退出等
public class UserService {
    private static final String SERVER_IP = "192.168.145.88";//192.168.247.88
    private static final int SERVER_PORT = 9999;
    private User user = new User();
    private Socket socket;
    public void setUserID(String ID) {
        user.setUserID(ID);
    }
    public UserService() {
    }
    public UserService(String ID) {
        this.setUserID(ID);
    }
    //用户登录检查
    public boolean checkUser(String ID, String pwd) {
        user.setUserID(ID);
        user.setPassword(pwd);
        user.setType("commen");
        boolean b = false;
        try {
            socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
            NetUtils.sendUser(socket, user);
            Message ms = NetUtils.receiveMessage(socket);
            if (ms.getMessageType().equals(MessageType.LOGIN_SUCCESS)) {
                ReceiveThread receiveThread = new ReceiveThread(user.getUserID());
                SendThread sendThread = new SendThread(user.getUserID());
                ManageCCST.addCCST(user.getUserID(), socket, sendThread, receiveThread);
                MessageService.MessageMap.put(ID, new LinkedBlockingQueue<>());
                receiveThread.start();
                sendThread.start();
                b = true;
            } else {
                NetUtils.closeSocket(socket);
                System.out.println("登陆失败，请重新输入账号或密码");
            }
        } catch (IOException e) {
            System.out.println("登录连接异常");
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
        return b;
    }
    //注册请求
    public boolean newUser(String ID, String pwd)  {
        user.setUserID(ID);
        user.setPassword(pwd);
        user.setType("newUser");
        try {
            socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
            NetUtils.sendUser(socket, user);
            Thread.sleep(1000);
            Message ms1 = NetUtils.receiveMessage(socket);
            if (ms1.getMessageType().equals(MessageType.REGISTER_FAILURE)) {
                NetUtils.closeSocket(socket);
                return false;
            }
            else{
                NetUtils.closeSocket(socket);
                return true;
            }
        } catch (IOException e) {
            System.out.println("创建用户连接异常");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }return false;
    }
        //无异常退出
        public void exit() {
            Message message = new Message();
            message.setSender(user.getUserID());
            message.setMessageType(MessageType.CLIENT_EXIT);
            try {
                NetUtils.sendMessage(ManageCCST.getSocket(user.getUserID()), message);
                boolean messageSent = SendThread.waitForQueueEmpty(5000, user.getUserID());
                if (!messageSent) {
                    System.out.println("部分消息可能未发送，请稍后检查。");
                }
            } finally {
                SendThread sendThread = ManageCCST.getSendThread(user.getUserID());
                ReceiveThread receiveThread = ManageCCST.getReceiveThread(user.getUserID());
                if (sendThread != null) sendThread.stopThread();
                if (receiveThread != null) receiveThread.stopThread();
                ManageCCST.removeCCST(user.getUserID());
                System.out.println("用户 " + user.getUserID() + " 已成功退出。");
            }
        }
    }

