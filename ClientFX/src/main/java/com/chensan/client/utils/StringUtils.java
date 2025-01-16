package com.chensan.client.utils;

import java.util.Scanner;

/**
 * 字符串工具类
 */
public class StringUtils {
    static Scanner scanner = new Scanner(System.in);
    /**
     * 读取字符串，限制长度为limit，默认为空串
     *
     * @param limit 限制长度
     * @return 读取到的字符串
     */
    public static String readString(int limit) {
        return readKeyBoard(limit, false);
    }
    public static String readString(int limit, String defaultValue) {
        String str = readKeyBoard(limit, true);
        return str.equals("")? defaultValue : str;
    }
    public static char readConfirmSelection() {
        System.out.println("请输入你的选择(Y/N): 请小心选择");
        char c;
        for (; ; ) {
            String str = readKeyBoard(1, false).toUpperCase();
            c = str.charAt(0);
            if (c == 'Y' || c == 'N') {
                break;
            } else {
                System.out.print("选择错误，请重新输入：");
            }
        }
        return c;
    }
    /**
     * 读取键盘输入，限制长度为limit，默认为空串
     *
     * @param limit 限制长度
     * @return 读取到的字符串
     */
    private static String readKeyBoard(int limit, boolean blankReturn) {
        String line = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.length() == 0) {
                if (blankReturn) return line;
                else continue;
            }
            if (line.length() < 1 || line.length() > limit) {
                System.out.print("输入长度（不能大于" + limit + "）错误，请重新输入：");
                continue;
            }
            break;
        }
        return line;
    }
    public static String readNextLine(){
        String answer = scanner.nextLine();
        return answer;
    }
    public static String readNextChoice(){
        String answer = String.valueOf(scanner.nextInt());
        return answer;
    }
}

