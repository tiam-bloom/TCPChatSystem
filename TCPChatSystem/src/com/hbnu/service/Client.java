package com.hbnu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hbnu.pojo.User;
import com.hbnu.utils.ReadSendUtils;
import com.hbnu.view.HomePage;
import com.hbnu.view.IndexPage;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * @Author Tiam
 * @Date 2022/11/7 14:00
 * @Description: 客户端业务层, 对应 ServerThread服务端线程
 */
public class Client {
    private Socket socket;
    private ReadSendUtils rs;

    public Client(Socket socket) {
        this.socket = socket;
        rs = new ReadSendUtils(socket);
    }


    /**
     * 是否退出状态
     */
    private boolean isOver = true;
    /**
     * 是否登录状态
     */
    private boolean isLogin = false;

    /**
     * 启动客户端
     */
    public void clientView() {
        while (isOver) {
            homeFunction();
            while (isLogin) {
                indexFunction();
            }
        }
    }

    /**
     * 系统首页
     */
    private void homeFunction() {
        // 获取用户输入的命令
        String order = HomePage.home();
        // 发送命令到服务端
        rs.sendString(order);
        switch (order) {
            case "1":
                register();
                break;
            case "2":
                login();
                break;
            case "3":
                findPwd();
                break;
            case "4":
                quitSystem();
                break;
            case "0":
                systemAdmin();
                break;
            default:
                System.out.println("指令异常!");
                System.exit(-1);
        }
    }

    /**
     * 主页功能
     */
    private void indexFunction() {

        String order = IndexPage.index();
        rs.sendString(order);
        switch (order) {
            case "1":
                lookOnlineUsers();
                break;
            case "2":
                privateChat();
                break;
            case "3":
                groupChat();
                break;
            case "4":
                delAccount();
                break;
            case "5":
                modifyPwd();
                break;
            case "6":
                quitLogin();
                break;
            default:
                System.out.println("异常指令 => " + order);
                System.exit(-1);
        }
    }

    /**
     * 0. 进入管理员页面,输入密码查询所有用户信息
     */
    private void systemAdmin() {
        // 输入密码, 发送到服务端效验
        String password = HomePage.verifyIdentity();
        rs.sendString(password);
        // 读取密码校验结果
        String isTrue = rs.readString();
        if (isTrue.charAt(0) == '0') {
            System.out.println(isTrue.substring(1));
            return;
        }
        // 读取管理员要进行的操作
        String operator = HomePage.getOperator();
        rs.sendString(operator);

        // 读取结果
        String message = rs.readString();
        switch (message.charAt(0)) {
            case '1':
                // 反序列化JSON字符串, 转为对象
                List<User> list = JSON.parseObject(message.substring(1), new TypeReference<List<User>>() {
                });
                // 打印结果
                list.forEach(System.out::println);
                break;
            case '2':
                Map<Integer, User> socketUserMap = JSON.parseObject(message.substring(1), new TypeReference<Map<Integer, User>>() {
                });
                System.out.println("端口 => 用户信息(未登录为null)");
                socketUserMap.forEach((k, v) -> System.out.println(k + " => " + v));
                break;
            default:
                System.out.println("结果异常" + message);
                System.exit(-1);
        }
    }

    /**
     * 3. 找回密码
     */
    private void findPwd() {
        // 1. 获取用户输入的账号 和 邮箱, 并发送到服务端
        User user = HomePage.findPwdPage();
        rs.send(user);
        // 2. 读取服务端的校验结果
        String message = rs.readString();
        // 如果验证码发送成功
        if (message.charAt(0) == '4') {
            String incode = HomePage.inputEmailCode();
            rs.sendString(incode);
            // 接收服务端对验证码的校验结果
            message = rs.readString();
        }
        System.out.println(message.substring(1));
    }

    /**
     * 1. 注册
     */
    private void register() {
        // 1. 获取用户输入的注册信息, 封装到User对象
        User user = HomePage.registerPage();
        // 2. 发送给服务端
        rs.send(user);
        // 3. 读取服务端返回的结果
        String message = rs.readString();
        // 4. 处理结果
        System.out.println(message);
    }

    /**
     * 4. 退出系统
     */
    private void quitSystem() {
        // 确认用户是否退出
        boolean certify = HomePage.exitSystem();
        rs.send(certify);
        if (certify) {
            isOver = false;
            try {
                socket.close();
                System.out.println("已退出系统");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 2. 登录
     */
    private void login() {
        // 1. 读取用户输入的登录信息
        User user = HomePage.loginPage();
        // 2. 发送到服务端效验
        rs.send(user);
        // 3. 接收服务端的校验结果
        String message = rs.readString();
        // 4. 处理结果
        if (message.charAt(0) == '1') {
            isLogin = true;
            // 开启读消息线程
            ClientThread.isExit = false;
            new Thread(new ClientThread(socket)).start();
        }
        System.out.println(message.substring(1));
    }

    /**
     * 5. 修改密码
     */
    private void modifyPwd() {
        String pwd = IndexPage.modifyPwdPage();
        rs.sendString(pwd);
        // todo 判断是否修改成功,再退出
        String message = rs.readString();
        // System.out.println(message+"read by me");
        if (message.charAt(0)=='1'){
            quitLogin();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 4. 注销用户
     */
    private void delAccount() {
        // 确认用户是否真的要注销
        boolean certify = IndexPage.are_you_sure();
        rs.send(certify);
        // 如果确定注销, 退出登录, todo 判断是否注销成功
        if (certify) {
            quitLogin();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 查看在线用户
     */
    private void lookOnlineUsers() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // todo 与线程读消息冲突,
        // 读消息线程会接受服务端发送的在线人员名单, 所以此处不再进行读取

        // String onlineUsers = rs.readString();
        // System.out.println("我读的"+onlineUsers);
    }

    /**
     * 6. 退出登录
     */
    private void quitLogin() {
        // 修改登录状态
        isLogin = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //终止线程
        //通过服务端发送退出标志, 结束线程

        //使用stop方法是很危险的，就象突然关闭计算机电源，而不是按正常程序关机一样，
        // 可能会产生不可预料的结果，因此，并不推荐使用stop方法来终止线程。
        // thread.stop();
    }

    /**
     * 3. 群聊
     */
    private void groupChat() {
        while (true) {
            String message = IndexPage.inputMessage();
            rs.sendString(message);
            if(message.equalsIgnoreCase("q")) break;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 私聊
     */
    private void privateChat() {
        while (true) {
            // 输入消息
            String nameMessage = IndexPage.inputPrivateMessage();
            // 发送消息到服务端
            rs.sendString(nameMessage);
            if(nameMessage.equalsIgnoreCase("q")) break;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
