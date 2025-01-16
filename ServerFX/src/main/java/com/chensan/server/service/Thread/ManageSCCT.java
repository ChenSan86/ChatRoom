package com.chensan.server.service.Thread;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 管理SCCT线程，包括创建、销毁、获取连接
 */
public class ManageSCCT {
    private static ConcurrentHashMap<String,SCCTPair> SCCTMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,SCCTPair> getSCCTMap() { return SCCTMap; }
    public static void addCCST(String key, Socket socket,SendThread sendThread, ReceiveThread receiveThread) {
        SCCTMap.put(key, new SCCTPair(socket,sendThread, receiveThread));
    }
    public static void removeCCST(String userID) {
        SCCTMap.remove(userID);
    }
    public static SCCTPair getCCST(String userID) { return SCCTMap.get(userID); }
    public static SendThread getSendThread(String userID) { return SCCTMap.get(userID).getSendThread(); }
    public static ReceiveThread getReceiveThread(String userID) { return SCCTMap.get(userID).getReceiveThread(); }
    public static Socket getSocket(String userID){return SCCTMap.get(userID).getSocket();}
    public static String getFriendList() {
        String friendList = "";
        for(String userid : SCCTMap.keySet())
            friendList += "  " + userid;
        return friendList;
    }
}
