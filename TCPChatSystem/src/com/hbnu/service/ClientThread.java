package com.hbnu.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @Author Tiam
 * @Date 2022/11/6 22:06
 * @Description: 客户端 读消息线程, 登录成功后开启
 */
public class ClientThread implements Runnable {
    Socket socket;

    public static boolean isExit = false;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!isExit) {
            try {
                readMessage();
            } catch (Exception e) {
                System.out.println("服务器断开连接");
                System.exit(-1);
            }
        }
        // System.out.println("线程停止");
    }

    /**
     * 线程: 读取其他用户的消息
     */
    public String readMessage() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // System.out.println("开始接受消息... ");
        String message = in.readLine();
        if ("exit".equalsIgnoreCase(message)) {
            isExit = true;
            System.out.println("\n已退出登录");
            return null; //停止方法
        }
        System.out.println("" + message);
        return message;
    }

}
