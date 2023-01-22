package com.hbnu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hbnu.controller.RunServer;
import com.hbnu.dao.UserDao;
import com.hbnu.dao.UserDaoImpl;
import com.hbnu.pojo.User;
import com.hbnu.utils.GenerateCode;
import com.hbnu.utils.ReadSendUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author Tiam
 * @Date 2022/11/7 13:34
 * @Description: 服务端线程
 * 统一规定 返回消息 第一位为状态码
 */
public class ServerThread implements Runnable {

    @Override
    public void run() {
        serverTaskThread();
    }

    /**
     * 当前服务端线程连接的socket对象
     */
    private Socket socket;
    /**
     * 判断用户是否已退出, 退出结束该线程, 关闭资源
     */
    private boolean isOver = true;
    /**
     * 判断用户是否已登录
     */
    private boolean isLogin = false;
    /**
     * 数据库操作对象
     */
    private UserDao userDao = new UserDaoImpl();
    /**
     * 读写工具类, 用于向客户端发送消息和接收客户端发送的消息
     */
    private ReadSendUtils rs;

    public ServerThread(Socket socket) {
        this.socket = socket;
        rs = new ReadSendUtils(socket);
    }

    /**
     * 服务端线程任务
     */
    private void serverTaskThread() {
        while (isOver) {
            homeFunction();
            while (isLogin) {
                indexFunction();
            }
        }
    }

    /**
     * 首页功能
     */
    public void homeFunction() {
        // System.out.println("等待读取首页命令...");
        String order = rs.readString();
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
        // System.out.println("等待读取主页命令...");
        String order = rs.readString();
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
                System.out.println("指令异常! => " + order);
                System.exit(-1);
        }
    }

    /**
     * 0. 管理员业务
     */
    private void systemAdmin() {
        // 读取管理员密码
        String pwd = rs.readString();
        // 验证密码, 不正确发送回提示信息, 停止执行
        if (!"yujing".equalsIgnoreCase(pwd)) {
            rs.sendString("0密码错误");
            return;
        }
        rs.sendString("1管理员密码正确");
        // 读取管理员要进行的操作
        String operator = rs.readString();
        String message = "";
        switch (operator) {
            case "1":
                // 查看所有已注册用户
                List<User> allUsers = userDao.findAllUsers();
                // 将集合转为JSON字符串格式
                message = "1" + JSON.toJSONString(allUsers);
                break;
            case "2":
                HashMap<Integer, User> map = new HashMap<>();
                for (Socket socket1 : RunServer.socketUserMap.keySet()) {
                    map.put(socket1.getPort(), RunServer.socketUserMap.get(socket1));
                }
                // 无法解析Socket对象, 替换为其端口
                // SerializerFeature.WriteMapNullValue, 不忽略NULL值
                message = "2" + JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
                break;
            default:
                System.out.println("指令异常=>" + operator);
                System.exit(-1);
        }
        rs.sendString(message);
    }

    /**
     * 3. 找回密码
     */
    private void findPwd() {
        // 1. 接收客户端的用户信息, 并在数据库中查询
        User user = (User) rs.read();
        User userByFind = userDao.findByUsername(user.getUsername());

        String message;
        if (userByFind == null) {
            message = "0用户未注册";
        } else if (!userByFind.getEmail().equalsIgnoreCase(user.getEmail())) {
            message = "2邮箱不正确";
        } else {
            // 邮箱正确, 发送验证码
            String res = GenerateCode.sendAuthCodeEmail(user.getEmail());
            if (res == null) {
                message = "3验证码发送失败";
                rs.sendString(message);
            } else {
                message = "4验证码发送成功, 请检查你的邮箱";
                rs.sendString(message);
                // 读取客户端输入的验证码
                String incode = rs.readString();
                if (res.equals(incode)) {
                    message = "1您的密码是: " + userByFind.getPassword();
                } else {
                    message = "5验证码错误!";
                }
            }
        }
        // 发送结果
        rs.sendString(message);
    }

    /**
     * 1. 注册
     */
    private void register() {
        // 1. 读取客户端发送过来的用户注册信息
        User user = (User) rs.read();
        // 2. 在数据库中查询是否已注册
        User userByFind = userDao.findByUsername(user.getUsername());
        System.out.print(socket.getPort() + ":" + user);
        String message;
        if (userByFind != null) {
            message = "用户已注册, 请直接登录";
        } else {
            // 3. 如未注册, 将用户的信息插入到数据库
            int i = userDao.insertUser(user);
            message = i == 1 ? "注册成功" : "注册失败";
        }
        System.out.println(message);
        // 4. 向客户端发送注册结果
        rs.sendString("【" + message + "】");
    }

    /**
     * 4. 退出系统
     */
    private void quitSystem() {
        // 读取用户是否确认退出
        boolean certify = (boolean) rs.read();
        // 不确定 停止执行
        if (!certify) return;

        isOver = false;
        System.out.print(socket.getPort() + "断开连接, ");
        RunServer.socketUserMap.remove(socket);
        System.out.println("剩余连接用户: " + RunServer.socketUserMap.size());
        // 遍历当前已连接的用户
        RunServer.socketUserMap.forEach((key, value) -> System.out.println(key + "=>" + value));
        //todo退出系统关闭资源
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 登录
     */
    private void login() {
        // 读取用户输入的User对象
        User user = (User) rs.read();
        System.out.print(socket.getPort() + ":" + user);
        // 查询校验
        User realUser = userDao.findByUsername(user.getUsername());
        int code;
        String message;
        if (realUser == null) {
            code = 0;
            message = "【用户未注册!】";
        } else if (findSocketByUsername(user.getUsername()) != null) {
            code = 2;
            message = "【用户已登录】";
        } else if (!realUser.getPassword().equals(user.getPassword())) {
            code = 3;
            message = "【密码错误】";
        } else {
            code = 1;
            message = "【登录成功】";
            isLogin = true;
            RunServer.socketUserMap.put(socket, user);
            // 给其他所有用户看
            sendGroupMes("【" + user.getUsername() + "】已上线", false);
        }
        // 把校验结果发送回客户端
        rs.sendString(code + message);
        System.out.println(message);
    }

    /**
     * 5. 修改密码
     */
    private void modifyPwd() {
        // 1. 接收客户端输入的新密码和旧密码, 并进行处理
        String pwd = rs.readString();
        String[] pwds = pwd.split(" ");
        String oldPwd = pwds[0];
        String newPwd = pwds[1];
        // 2. 获取当前发送修改密码需求的用户名, 并在数据库中查询校验旧密码是否正确
        String name = RunServer.socketUserMap.get(socket).getUsername();
        User userByFind = userDao.findByUsername(name);

        String message;
        // 3. 处理结果
        int i = 0;
        if (oldPwd.equals(userByFind.getPassword())) {
            i = userDao.updatePwd(name, newPwd);
            message = i == 1 ? "1修改成功, 登录身份已过期, 请重新登录" : "2修改失败";
        } else {
            message = "3旧密码错误";
        }
        // todo 为什么第一个一定是发给 读线程的?不是主线程的
        // 第一个发送给读消息线程, 用于打印提示
        rs.sendString(message.substring(1));
        // 第二个发送给主线程, 用于判断 密码是否修改成功
        rs.sendString(message);

        System.out.println(name + " " + message);
        if (i == 1) quitLogin();
    }

    /**
     * 4. 注销账户
     */
    private void delAccount() {
        boolean certify = (boolean) rs.read();
        // 如果用户确认注销账号,
        if (certify) {
            String name = RunServer.socketUserMap.get(socket).getUsername();
            // 从表中删除他的信息
            userDao.deleteUser(name);
            System.out.println("【" + name + "】已注销账户");
            rs.sendString("账户已注销, 请重新登录");
            // 退出登录
            quitLogin();
        }
    }

    /**
     * 1. 查看在线用户
     */
    private void lookOnlineUsers() {
        // 查找所有已登录用户
        List<String> list = new ArrayList<>();
        for (Socket socket1 : RunServer.socketUserMap.keySet()) {
            User user = RunServer.socketUserMap.get(socket1);
            if (user != null) {
                list.add(user.getUsername());
            }
        }
        rs.sendString("当前在线用户: " + list.toString());
    }

    /**
     * 6. 退出登录
     */
    private void quitLogin() {
        isLogin = false;
        String tips = "【" + RunServer.socketUserMap.get(socket).getUsername() + "】已下线";
        System.out.println(tips);
        // 清空map中对应的User对象
        RunServer.socketUserMap.put(socket, null);
        // 给所有其他在线用户发送 下线信息
        sendGroupMes(tips, false);
        // 发送停止读取线程命令, 客户端停止接收消息
        rs.sendString("exit");
    }

    /**
     * 3. 群聊
     */
    private void groupChat() {
        while (true) {
            String message = rs.readString();
            if (message.equalsIgnoreCase("q")) {
                break;
            }
            message = "【群发消息】" + RunServer.socketUserMap.get(socket).getUsername() + ": " + message;
            System.out.println(message);
            // 发送给所有在线人员, 包括自己
            sendGroupMes(message, true);
        }
    }

    /**
     * 2. 私聊
     */
    private void privateChat() {
        while (true) {
            // 读取消息
            String nameMessage = rs.readString();
            if (nameMessage.equalsIgnoreCase("q")) break;

            // 处理消息 获得发送对象用户名 及消息
            String toName = nameMessage.split(" ")[0];
            String toMessage = "【" + RunServer.socketUserMap.get(socket).getUsername() + "】:" + nameMessage.substring(toName.length());
            Socket toSocket = findSocketByUsername(toName);
            System.out.println(toName + "\t" + toMessage + "\t");

            int code;
            String message;
            if (toSocket != null) {
                // 转发给目标用户
                new ReadSendUtils(toSocket).sendString(toMessage);
                code = 1;
                message = "发送成功";
            } else {
                code = 0;
                message = "未找到目标用户";
                rs.sendString(message);
            }
            // 服务端打印提示
            System.out.println(code + message);
        }
    }

    /**
     * 根据用户名查找其socket
     *
     * @param username
     * @return 返回null => 用户未登录
     */
    private Socket findSocketByUsername(String username) {
        for (Socket socket1 : RunServer.socketUserMap.keySet()) {
            User user = RunServer.socketUserMap.get(socket1);
            if (user != null && user.getUsername().equals(username)) {
                return socket1;
            }
        }
        return null;
    }


    /**
     * 给所有在线用户(已登录)发送消息
     *
     * @param mes    需要发送的消息
     * @param isToMe 是否给自己发
     * @return 返回已发送的人数
     */
    private int sendGroupMes(String mes, boolean isToMe) {
        int count = 0;
        for (Socket socket1 : RunServer.socketUserMap.keySet()) {
            if (RunServer.socketUserMap.get(socket1) != null) {
                // 跳过自己
                if (!isToMe && socket1 == this.socket) continue;
                new ReadSendUtils(socket1).sendString(mes);
                count++;
            }
        }
        return count;
    }

}
