package com.chensan.client.view;

import com.chensan.client.service.MessageService;
import com.chensan.client.service.UserService;
import com.chensan.client.utils.StringUtils;
public class ClientView {
    boolean loop = true;
    UserService userservice = new UserService();


    String key;

    public void mainMenu() {
        while (loop) {
            System.out.println("==========用户登录==========");
            System.out.println("1\t\t\t登录");
            System.out.println("2\t\t\t注册");
            System.out.println("9\t\t\t退出");
            System.out.println("请输入你的选择： ");
            key = StringUtils.readString(1);
            switch (key) {
                case "1":
                    System.out.println("请输入账号： ");
                    String ID = StringUtils.readString(50);//后期可修改账号长度
                    System.out.println("请输入密码： ");
                    String pwd = StringUtils.readString(50);//后期可限制密码长度
                    boolean secondLoop = userservice.checkUser(ID, pwd);
                    while (secondLoop) {
                        System.out.println("==========胃信登录成功！==========");
                        System.out.println("==========欢迎用户" + ID + "==========");
                        System.out.println("\t\t1.显示在线用户列表");
                        System.out.println("\t\t2.群发消息");
                        System.out.println("\t\t3.私聊消息");
                        System.out.println("\t\t4.私发文件");
                        System.out.println("\t\t5.群发文件");
                        System.out.println("\t\t9.退出登录");
                        System.out.println("请输入你的选择：");
                        String secondKey = StringUtils.readString(1);
                        switch (secondKey) {
                            case "1":
                                MessageService.getFriends(ID);
                                break;
                            case "2":
                                System.out.println("请输入广播消息： ");
                                String broadcast = StringUtils.readString(600);
                                MessageService.sendMessageToAll(broadcast,ID);
                                break;
                            case "3":
                                System.out.println("请输入接收者ID");
                                String receiverID = StringUtils.readString(50);
                                System.out.println("请输入私聊消息： ");
                                String content = StringUtils.readString(600);
                                MessageService.sendMessageToOne(content,ID,receiverID);
                                break;
                            case "4":
                                System.out.println("请输入文件接收者： ");
                                String receiverID1 = StringUtils.readString(100);
                                System.out.println("请输入文件地址");
                                String src = StringUtils.readString(100);

                                break;
                            case "5":
                                System.out.println("请输入文件地址");
                                String src1 = StringUtils.readString(100);

                                break;
                            case "9":
                                userservice.exit();
                                secondLoop = false;
                                break;
                        }
                    }
                    break;
                    case "2":
                    System.out.println("请输入账号： ");
                    String ID1 = StringUtils.readString(50);//后期可修改账号长度
                    System.out.println("请输入密码： ");
                    String pwd1 = StringUtils.readString(50);//后期可限制密码长度
                        boolean a = userservice.newUser(ID1, pwd1);
                        if ( a == true){
                            System.out.println("注册成功！请重新登录！");
                        }
                        else {
                            System.out.println("注册失败！账号已存在！");
                        }
                        break;
                case"9":
                    loop = false;
                    break;
            }
        }System.out.println("==========胃信已退出==========");
    }
}
