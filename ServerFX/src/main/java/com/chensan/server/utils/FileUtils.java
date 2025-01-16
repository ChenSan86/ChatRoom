package com.chensan.server.utils;

import com.chensan.common.Message;
import com.chensan.common.User;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FileUtils {
    private static final String FILE_PATH =
            "C:\\Users\\honor\\Desktop\\WeChat\\ServerFX\\src\\main\\java\\com\\chensan\\server\\data\\FileData";
    private static final String VALID_USER_PATH =
            "C:\\Users\\honor\\Desktop\\WeChat\\ServerFX\\src\\main\\java\\com\\chensan\\server\\data\\ValidUser";
    private static final String MESSAGE_PATH =
            "C:\\Users\\honor\\Desktop\\WeChat\\ServerFX\\src\\main\\java\\com\\chensan\\server\\data\\MessageData";

    // 写用户数据到文件
    public static void writeUserToFile(ConcurrentHashMap<String, User> userMap) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(VALID_USER_PATH))) {
            out.writeObject(userMap);
        }
    }

    // 读取用户数据，从文件中返回一个新的对象，如果文件为空
    public static ConcurrentHashMap<String, User> readUserFromFile() throws IOException, ClassNotFoundException {
        File file = new File(VALID_USER_PATH);
        if (!file.exists() || file.length() == 0) {
            // 如果文件不存在或文件为空，返回一个新的空ConcurrentHashMap
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(VALID_USER_PATH))) {
            return (ConcurrentHashMap<String, User>) in.readObject();
        }
    }

    // 写消息数据到文件
    public static void writeMessageToFile(ConcurrentHashMap<String, List<Message>> messageData) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MESSAGE_PATH))) {
            out.writeObject(messageData);
        }
    }

    // 读取消息数据，如果文件为空，返回一个新的对象
    public static ConcurrentHashMap<String, List<Message>> readMessageFromFile() throws IOException, ClassNotFoundException {
        File file = new File(MESSAGE_PATH);
        if (!file.exists() || file.length() == 0) {
            // 如果文件不存在或文件为空，返回一个新的空ConcurrentHashMap
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(MESSAGE_PATH))) {
            return (ConcurrentHashMap<String, List<Message>>) in.readObject();
        }
    }

    // 写文件消息数据到文件
    public static void writeFileMessageToFile(ConcurrentHashMap<String, HashMap<String, Message>> messageData) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(messageData);
        }
    }

    // 读取文件消息数据，如果文件为空，返回一个新的对象
    public static ConcurrentHashMap<String, HashMap<String, Message>> readFileMessageFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            // 如果文件不存在或文件为空，返回一个新的空ConcurrentHashMap
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (ConcurrentHashMap<String, HashMap<String, Message>>) in.readObject();
        }
    }
}
