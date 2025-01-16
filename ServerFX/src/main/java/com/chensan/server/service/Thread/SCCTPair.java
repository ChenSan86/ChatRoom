package com.chensan.server.service.Thread;

import java.net.Socket;

public class SCCTPair {
    private Socket socket;
    private SendThread sendThread;
    private ReceiveThread receiveThread;
    private String userID;

    public SCCTPair(Socket socket,SendThread sendThread, ReceiveThread receiveThread) {
        this.sendThread = sendThread;
        this.receiveThread = receiveThread;
        this.socket = socket;

    }


    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public SendThread getSendThread() {
        return sendThread;
    }

    public void setSendThread(SendThread sendThread) {
        this.sendThread = sendThread;
    }

    public ReceiveThread getReceiveThread() {
        return receiveThread;
    }

    public void setReceiveThread(ReceiveThread receiveThread) {
        this.receiveThread = receiveThread;
    }
}