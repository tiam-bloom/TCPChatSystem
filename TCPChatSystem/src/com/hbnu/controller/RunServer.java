package com.hbnu.controller;

import com.hbnu.pojo.User;
import com.hbnu.service.ServerThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tiam
 * @Date 2022/11/7 13:11
 * @Description: 服务端线程启动类
 * Finished By Tiam in 2022/11/12
 */
public class RunServer {
    /**
     * 线程安全的Map ConcurrentHashMap, 存储已连接的用户socket, 登录后存储其对应的User
     */
    public static Map<Socket, User> socketUserMap = new HashMap<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("服务端"+ InetAddress.getLocalHost().getHostAddress() +"已开启, 等待客户端连接...");

            while (true) {
                Socket mySocket = serverSocket.accept();
                socketUserMap.put(mySocket, null);

                System.out.println("客户端 " + mySocket.getPort() + " 已连接");
                System.out.println("目前已连接用户: " + socketUserMap.size());

                new Thread(new ServerThread(mySocket)).start();
            }
        } catch (IOException e) {
            System.out.println("连接异常中断!");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}
