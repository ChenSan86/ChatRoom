package com.chensan.client.service.Tread;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 管理CCST线程，包括创建、销毁、获取连接
 */
public class ManageCCST {
    public static ConcurrentHashMap<String,CCSTPair> CCSTMap = new ConcurrentHashMap<>();
    public static void addCCST(String key, Socket socket,SendThread sendThread, ReceiveThread receiveThread) {
        CCSTMap.put(key, new CCSTPair(socket,sendThread, receiveThread));
    }
    public static void removeCCST(String userID) {
        CCSTMap.remove(userID);
    }
    public static SendThread getSendThread(String userID) { return CCSTMap.get(userID).getSendThread(); }
    public static ReceiveThread getReceiveThread(String userID) { return CCSTMap.get(userID).getReceiveThread(); }
    public static Socket getSocket(String userID){return CCSTMap.get(userID).getSocket();}
}
