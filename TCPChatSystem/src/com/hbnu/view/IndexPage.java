package com.hbnu.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @Author Tiam
 * @Date 2022/11/7 12:57
 * @Description: 系统主页视图层(登录成功后进入)
 */
public class IndexPage {

    private static Scanner scanner = new Scanner(System.in);
    /**
     * 指令集
     */
    private static Map<String, String> map = new HashMap<>(6);

    static {
        map.put("1", "查看在线人员名单");
        map.put("2", "私聊");
        map.put("3", "群聊");
        map.put("4", "账号注销");
        map.put("5", "修改密码");
        map.put("6", "退出");
    }

    /**
     * 系统主页
     *
     * @return
     */
    public static String index() {
        System.out.println("系统主页");
        return HomePage.inputOrder(map);
    }

    /**
     * 4. 注销账户确认页面
     *
     * @return
     */
    public static boolean are_you_sure() {
        System.out.println("确定要注销账号吗? 该操作将不可撤销(Y/N)");
        do {
            System.out.print("$:");
            String yOrn = scanner.nextLine();
            if ("Y".equalsIgnoreCase(yOrn)) {
                return true;
            } else if ("N".equalsIgnoreCase(yOrn)) {
                return false;
            } else {
                System.out.println("指令有误, 请重新输入.");
            }
        } while (true);
    }

    /**
     * 2. 私聊界面
     *
     * @return
     */
    static int firstInput1 = 0;

    public static String inputPrivateMessage() {
        // 第一次输入时显示提示
        if (firstInput1++ == 0)
            System.out.println("请输入私聊对象用户名+你要发送的消息, eg: 小陈 你好! (输入q退出)");
        String message;
        while (true) {
            System.out.print("请输入:");
            message = scanner.nextLine();
            if (message.trim().contains(" ") || message.equalsIgnoreCase("q")) break;
            System.out.println("格式错误, 请重新输入");
        }
        return message;
    }

    /**
     * 3. 群聊界面
     *
     * @return
     */
    static int firstInput2 = 0;

    public static String inputMessage() {
        if (firstInput2++ == 0)
            System.out.print("请输入群聊消息(输入q退出):");
        return scanner.nextLine();
    }

    /**
     * 5. 修改密码界面
     *
     * @return 返回以空格间隔的 旧密码+新密码
     */
    public static String modifyPwdPage() {
        // todo限定密码不可有空格
        System.out.print("请输入旧密码:");
        String oldPwd = scanner.nextLine();
        String newPwd = null;
        while (true) {
            System.out.print("请输入新密码:");
            newPwd = scanner.nextLine();
            if (Pattern.matches("\\w{3,10}", newPwd)) break;
        }
        return oldPwd + " " + newPwd;
    }
}
