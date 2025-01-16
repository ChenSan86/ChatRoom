package com.chensan.server.utils;

import com.chensan.common.Message;
import com.chensan.common.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StreamUtil工具类
 * 负责管理Socket的输入输出流以及消息的发送和接收
 */
public class NetUtils {
    private static final ConcurrentHashMap<Socket, ObjectOutputStream> outputStreamMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Socket, ObjectInputStream> inputStreamMap = new ConcurrentHashMap<>();

    /**
     * 获取输出流
     */
    private static ObjectOutputStream getOutputStream(Socket socket) throws IOException {
        ObjectOutputStream oos = outputStreamMap.get(socket);
        if (oos == null) {
            synchronized (socket) { // 以 socket 为锁，确保线程安全
                oos = outputStreamMap.get(socket);
                if (oos == null) { // 双重检查
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    outputStreamMap.put(socket, oos);
                }
            }
        }
        return oos;
    }

    /**
     * 获取输入流
     */
    private static ObjectInputStream getInputStream(Socket socket) throws IOException {
        ObjectInputStream ois = inputStreamMap.get(socket);
        if (ois == null) {
            synchronized (socket) { // 以 socket 为锁，确保线程安全
                ois = inputStreamMap.get(socket);
                if (ois == null) { // 双重检查
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
            }
        }
    }

    /**
     * 接收用户对象
     */
    public static User receiveUser(Socket socket) {
        if (!socket.isClosed()) {
            try {
                ObjectInputStream ois = getInputStream(socket);
                return (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("接收消息时出错：" + e.getMessage());
            }
        }
        return null;
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
                // 忽略 EOF 和 Socket 异常，直接返回 null
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