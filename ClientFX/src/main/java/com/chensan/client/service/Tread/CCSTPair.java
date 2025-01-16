package com.chensan.client.service.Tread;
import java.net.Socket;
// Client connect Server Thread Pair
public class CCSTPair {
    private SendThread sendThread;
    private ReceiveThread receiveThread;
    private Socket socket;


    public CCSTPair(Socket socket,SendThread sendThread, ReceiveThread receiveThread) {
        this.sendThread = sendThread;
        this.receiveThread = receiveThread;
        this.socket = socket;

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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}