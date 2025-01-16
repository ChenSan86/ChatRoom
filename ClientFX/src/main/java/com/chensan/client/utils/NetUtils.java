package com.chensan.client.utils;

import com.chensan.common.Message;
import com.chensan.common.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class NetUtils {
    private static final ConcurrentHashMap<Socket, ObjectOutputStream> outputStreamMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Socket, ObjectInputStream> inputStreamMap = new ConcurrentHashMap<>();
    /**
     * 获取输出流（线程安全）
     */
    private static ObjectOutputStream getOutputStream(Socket socket) throws IOException {
        ObjectOutputStream oos = outputStreamMap.get(socket);
        if (oos == null) {
            synchronized (socket) {
                oos = outputStreamMap.get(socket);
                if (oos == null) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    outputStreamMap.put(socket, oos);
                }
            }
        }
        return oos;
    }

    /**
     * 获取输入流（线程安全）
     */
    private static ObjectInputStream getInputStream(Socket socket) throws IOException {
        ObjectInputStream ois = inputStreamMap.get(socket);
        if (ois == null) {
            synchronized (socket) {
                ois = inputStreamMap.get(socket);
                if (ois == null) {
                    ois = new ObjectInputStream(socket.getInputStream());
                    inputStreamMap.put(socket, ois);
                }
            }
        }
        return ois;
    }

    /**
     * 发送消息
     */
    public static void sendMessage(Socket socket, Message message) {
        if (!socket.isClosed()) {
            try {
                ObjectOutputStream oos = getOutputStream(socket);
                oos.writeObject(message);
            } catch (IOException e) {
                System.err.println("发送消息时出错：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送用户对象
     */
    public static void sendUser(Socket socket, User user) {
        if (!socket.isClosed()) {
            try {
                ObjectOutputStream oos = getOutputStream(socket);
                oos.writeObject(user);
            } catch (IOException e) {
                System.err.println("发送用户时出错：" + e.getMessage());
            }
        }
    }

    /**
     * 接收消息
     */
    public static Message receiveMessage(Socket socket) throws InterruptedException {
        if (!socket.isClosed()) {
            try {
                ObjectInputStream ois = getInputStream(socket);
                return (Message) ois.readObject();
            } catch (EOFException | SocketException e) {
                return null;
            } catch (IOException | ClassNotFoundException e) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("接收消息时线程被中断");
                }
                System.err.println("接收消息时发生错误：" + e.getMessage());
            }
        }
        return null;
    }
    /**
     * 安全关闭Socket及其流
     */
    public static void closeSocket(Socket socket) {
        if (socket != null) {
            try {
                ObjectOutputStream oos = outputStreamMap.remove(socket);
                if (oos != null) oos.close();
                ObjectInputStream ois = inputStreamMap.remove(socket);
                if (ois != null) ois.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("关闭Socket时出错：" + e.getMessage());
            }
        }
    }
}