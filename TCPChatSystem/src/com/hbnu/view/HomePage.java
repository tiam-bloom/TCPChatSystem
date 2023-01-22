package com.hbnu.view;

import com.hbnu.pojo.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @Author Tiam
 * @Date 2022/11/7 12:36
 * @Description: 系统主页视图层
 * 获取用户输入
 */
public class HomePage {
    private static Scanner scanner = new Scanner(System.in);
    /**
     * 指令集 map
     */
    private static Map<String, String> map = new HashMap<>(4);

    static {
        map.put("1", "注册");
        map.put("2", "登录");
        map.put("3", "找回密码");
        map.put("4", "退出");
    }

    /**
     * 系统首页
     * @return
     */
    public static String home() {
        System.out.println("系统首页（请输入对应数字选择功能）");
        return inputOrder(map);
    }

    /**
     * 1. 注册页
     *
     * @return
     */
    public static User registerPage() {
        String username = null;
        while (true) {
            System.out.print("请输入用户名:");
            username = scanner.nextLine();
            if (Pattern.matches("[\\u4e00-\\u9fa5A-Z]{2,6}", username)) break;
            System.out.println("不符合要求, 请重新输入哦(2到6位的中文或者大写字母组成)");
        }

        String password = null;
        while (true) {
            System.out.print("请输入密码: ");
            password = scanner.nextLine();
            if (Pattern.matches("\\w{3,10}", password)) break;
            System.out.println("不符合要求, 请重新输入哦  (3到10位, 只包括数字字母下划线)");
        }

        String email = null;
        while (true) {
            System.out.print("请输入你的邮箱:");
            email = scanner.nextLine();
            if (Pattern.matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}", email)) break;
            System.out.println("不符合要求, 请重新输入哦");
        }
        //封装User对象返回
        return new User(username, password, email);
    }

    /**
     * 2. 登录页
     *
     * @return
     */
    static int count = 0;

    public static User loginPage() {
        // todo限制尝试登录次数
        if (count >= 3) {
            try {
                System.out.println("小伙子不要暴力登录哦, 请等待10秒后再试");
                System.out.println("waiting...");
                Thread.sleep(1000 * 10);
                count = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print("请输入账号:");
        String name = scanner.nextLine();
        System.out.print("请输入密码:");
        String pwd = scanner.nextLine();
        count++;
        return new User(name, pwd, null);
    }

    /**
     * 3. 找回密码页
     *
     * @return
     */
    public static User findPwdPage() {
        System.out.print("请输入账号:");
        String name = scanner.nextLine();
        System.out.print("请输入邮箱:");
        String email = scanner.nextLine();
        return new User(name, null, email);
    }

    /**
     * 4. 退出系统页
     *
     * @return
     */
    public static boolean exitSystem() {
        System.out.println("确定退出吗?(Y/N)");
        while (true) {
            String yOrn = scanner.nextLine();
            switch (yOrn.trim()) {
                case "Y":
                case "y":
                    return true;
                case "N":
                case "n":
                    return false;
                default:
                    System.out.println("指令有误, 请重新输入. (Y/N)");
            }
        }
    }

    /**
     * 输入map中的指令
     *
     * @param map
     * @return 返回指令
     */
    protected static String inputOrder(Map<String, String> map) {
        map.forEach((key, value) -> System.out.print(key + ")" + value + "\t"));
        String order;
        while (true) {
            System.out.print("\n请输入指令:");
            order = scanner.nextLine();
            if (map.containsKey(order.trim()) || "0".equals(order.trim())) break;
            System.out.println("输入格式有误!请重新输入");
        }
        return order.trim();
    }

    /**
     * @return 返回输入的验证码
     */
    public static String inputEmailCode() {
        System.out.print("请输入你邮箱中的6位验证码:");
        String emailCode = scanner.nextLine();
        return emailCode;
    }

    /**
     * 验证管理员身份,
     *
     * @return 返回输入的管理员密码
     */
    public static String verifyIdentity() {
        System.out.print("请输入管理员密码: ");
        String pwd = scanner.nextLine();
        return pwd;
    }

    /**
     * 获取管理员的输入
     *
     * @return 返回指令
     */
    public static String getOperator() {
        System.out.println("1)查看用户信息表\t2)查看所有已连接客户端");
        String operator = null;
        while (true) {
            System.out.print("Please input order: ");
            operator = scanner.nextLine();
            if (operator.equals("1") || operator.equals("2")) break;
            System.out.println("老实点, 不要搞小动作哦");
        }
        return operator;
    }
}
